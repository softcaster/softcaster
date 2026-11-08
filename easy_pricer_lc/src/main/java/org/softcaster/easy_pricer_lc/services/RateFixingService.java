package org.softcaster.easy_pricer_lc.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.CashFlowMasterData;
import org.softcaster.core.data.FloatingRateTerms;
import org.softcaster.core.data.FltLoanMasterData;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.MarketQuote;
import org.softcaster.core.data.MarketQuoteDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.easy_pricer_mds_core.calc.RateFixing;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.SbcStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Fixing dei tassi variabili alla chiusura (step FIXING, prima di cedole e
 * ratei), per titoli (FRB / FRN) e mutui (FRM) in posizione.
 *
 * Ogni cedola / rata ancora stimata (ESTIMATED) con data di fixing (inizio del
 * periodo meno fixingLagDays) fino alla data ufficiale si fissa dal fixing
 * dell'indice (market_quote della market_quote_definition dell'indice, tasso
 * in decimale): tasso = indice + spread, entro floor e cap. La cedola passa a
 * RECORDED, le successive ancora stimate si proiettano allo stesso tasso
 * (ultima cedola) e il tasso dell'anagrafica diventa quello del periodo. Le
 * cedole gia' RECORDED (fissate esternamente) e PAID non si toccano. Un fixing
 * mancante blocca la chiusura: il periodo non si puo' maturare con un tasso
 * stimato.
 *
 * Interessi come nel piano originale:
 *   titoli: per 100 di nominale, 100 x tasso x frazione d'anno (accrual daycount);
 *   mutui italiani e bullet: residuo x tasso x frazione d'anno (daycount);
 *   mutui francesi: rata costante ricalcolata sul residuo e sulle rate restanti,
 *   tasso periodico = tasso / (12 / mesi del periodo), come
 *   FrenchAmortizationStrategy. Cambiano quote capitale e interessi.
 */
@Service
public class RateFixingService {

    private static final Logger log = LoggerFactory.getLogger(RateFixingService.class);
    private static final int SBC_ID = 1;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private MarketQuoteDAO marketQuoteDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public EodTaskResult fixRates() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate date = sbc.getOfficialDate();
        if (sbc.getStatus() != SbcStatus.CLOSING) {
            return EodTaskResult.failed(EodStep.FIXING, date, "Rate fixing runs only during the end of day");
        }

        // strumenti in posizione (non chiusa)
        Set<Integer> masterDataIds = new TreeSet<>();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                if (!PnlCalc.isFlat(detail.getBuyQty(), detail.getSellQty())) {
                    masterDataIds.add(detail.getMasterData());
                }
            }
        }

        int processed = 0;
        int created = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();
        for (Integer id : masterDataIds) {
            try {
                Integer fixed = transactionTemplate.execute(status -> fixInstrument(id, date));
                if (fixed == null) {
                    continue; // tasso fisso
                }
                processed++;
                if (fixed > 0) {
                    created += fixed;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                processed++;
                errors.add(e.getLocalizedMessage());
                log.error("### Rate fixing error for master data {}: {}", id, e.getLocalizedMessage());
            }
        }
        String message = errors.isEmpty() ? "" : String.join("; ", errors);
        return EodTaskResult.of(EodStep.FIXING, date, processed, created, skipped, errors.size(), message);
    }

    /**
     * @return cedole fissate, null se lo strumento non e' a tasso variabile
     */
    private Integer fixInstrument(Integer masterDataId, LocalDate date) {
        MasterData masterData = (MasterData) Hibernate.unproxy(entityManager.find(MasterData.class, masterDataId));
        FloatingRateTerms terms;
        if (masterData instanceof FltLoanMasterData loan) {
            terms = loan.getFloatingTerms();
        } else if (masterData instanceof FltSecurityMasterData bond) {
            terms = bond.getFloatingTerms();
        } else {
            return null;
        }
        CashFlowMasterData cfmd = (CashFlowMasterData) masterData;
        List<CashFlowItem> items = new ArrayList<>(cfmd.getCashFlows());
        items.sort(Comparator.comparing(CashFlowItem::getEnddate));
        Calendar calendar = new Calendar(masterData.getCurrencyList());

        int fixed = 0;
        for (int k = 0; k < items.size(); k++) {
            CashFlowItem item = items.get(k);
            if (item.getCashFlowStatus() != CashFlowStatus.ESTIMATED) {
                continue;
            }
            LocalDate fixingDate = RateFixing.fixingDate(item.getStartDate().toLocalDate(), terms, calendar);
            if (fixingDate.isAfter(date)) {
                break; // le successive si fissano piu' avanti
            }
            if (terms.getProjectionIndex() == null) {
                throw new IllegalStateException(masterData.getCode() + ": period from " + item.getStartDate()
                        + " not fixed and no index on the instrument");
            }
            MarketQuote fixing = marketQuoteDAO.findQuote(terms.getProjectionIndex().getMarketQuoteDefinitionId(), fixingDate);
            if (fixing == null || fixing.getPrice() == null) {
                throw new IllegalStateException(masterData.getCode() + ": missing " + terms.getProjectionIndex().getCode()
                        + " fixing of " + fixingDate + " for the period from " + item.getStartDate());
            }
            double indexRate = fixing.getPrice().doubleValue();
            double rate = RateFixing.couponRate(indexRate, terms);
            if (masterData instanceof FltLoanMasterData loan) {
                applyToLoan(loan, items, k, rate);
            } else {
                applyToBond((FltSecurityMasterData) masterData, items, k, rate);
            }
            item.setCashFlowStatus(CashFlowStatus.RECORDED);
            cfmd.setInterestRate(rate * 100.);
            fixed++;
            log.info("Fixing {} period {} - {}: index {} at {} = {}, rate {}", masterData.getCode(), item.getStartDate(),
                    item.getEnddate(), terms.getProjectionIndex().getCode(), fixingDate, indexRate, rate);
        }
        return fixed;
    }

    private static void applyToBond(FltSecurityMasterData bond, List<CashFlowItem> items, int k, double rate) {
        for (int j = k; j < items.size(); j++) {
            CashFlowItem item = items.get(j);
            if (j > k && item.getCashFlowStatus() != CashFlowStatus.ESTIMATED) {
                continue;
            }
            item.setInterest(100. * rate * bond.getAccrualDaycount().calculate(item.getStartDate().toLocalDate(),
                    item.getEnddate().toLocalDate(), bond.getFrequency()));
        }
    }

    private static void applyToLoan(FltLoanMasterData loan, List<CashFlowItem> items, int k, double rate) {
        double balance = loan.getPrincipalAmount();
        for (int j = 0; j < k; j++) {
            balance -= items.get(j).getAmount();
        }

        if (loan.getAmortizationSchedule() == AmortizationSchedule.SAS) {
            // preammortamento (rate senza quota capitale) fino alla prima rata di ammortamento
            int m = k;
            while (m < items.size() && isZero(items.get(m).getAmount())) {
                if (isOpen(items.get(m), m, k)) {
                    items.get(m).setInterest(balance * rate * yearFraction(loan, items.get(m)));
                }
                m++;
            }
            // rata costante sulle rate ancora aperte; le RECORDED restano come sono e
            // l'ultima aperta chiude il residuo
            List<Integer> open = new ArrayList<>();
            double fixedPrincipal = 0.;
            for (int j = m; j < items.size(); j++) {
                if (isOpen(items.get(j), j, k)) {
                    open.add(j);
                } else {
                    fixedPrincipal += items.get(j).getAmount();
                }
            }
            if (open.isEmpty()) {
                return;
            }
            int n = open.size();
            double periodic = rate / (12.0 / months(items.get(open.get(0))));
            double toAmortize = balance - fixedPrincipal;
            double payment = periodic == 0. ? toAmortize / n : toAmortize * (periodic / (1 - Math.pow(1 + periodic, -n)));
            int lastOpen = open.get(n - 1);
            for (int j = m; j < items.size(); j++) {
                CashFlowItem item = items.get(j);
                if (!isOpen(item, j, k)) {
                    balance -= item.getAmount();
                    continue;
                }
                double interest = balance * periodic;
                double principal = j == lastOpen ? balance - remainingFixed(items, j, k) : payment - interest;
                item.setInterest(interest);
                item.setAmount(principal);
                balance -= principal;
            }
        } else {
            // italiano e bullet: quote capitale invariate, interessi delle sole rate aperte
            for (int j = k; j < items.size(); j++) {
                CashFlowItem item = items.get(j);
                if (isOpen(item, j, k)) {
                    item.setInterest(balance * rate * yearFraction(loan, item));
                }
                balance -= item.getAmount();
            }
        }
    }

    /**
     * Rata da ricalcolare: quella che si sta fissando o una successiva ancora
     * stimata (non RECORDED ne' PAID).
     */
    private static boolean isOpen(CashFlowItem item, int j, int k) {
        return j == k || item.getCashFlowStatus() == CashFlowStatus.ESTIMATED;
    }

    /**
     * Quote capitale delle rate gia' fissate dopo la rata j.
     */
    private static double remainingFixed(List<CashFlowItem> items, int j, int k) {
        double sum = 0.;
        for (int i = j + 1; i < items.size(); i++) {
            if (!isOpen(items.get(i), i, k)) {
                sum += items.get(i).getAmount();
            }
        }
        return sum;
    }

    private static double yearFraction(FltLoanMasterData loan, CashFlowItem item) {
        return loan.getDaycount().calculate(item.getStartDate().toLocalDate(), item.getEnddate().toLocalDate(), loan.getFrequency());
    }

    /**
     * Mesi del periodo arrotondati, come FrenchAmortizationStrategy.
     */
    private static int months(CashFlowItem item) {
        long days = ChronoUnit.DAYS.between(item.getStartDate().toLocalDate(), item.getEnddate().toLocalDate());
        return (int) Math.max(1, Math.round(days / (365.25 / 12)));
    }

    private static boolean isZero(Double value) {
        return value == null || Math.abs(value) < 1e-9;
    }
}
