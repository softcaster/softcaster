package org.softcaster.easy_pricer_lc.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.RequestType;
import org.softcaster.engine.enums.SbcStatus;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Rolling degli FX spot alla chiusura di D, per le posizioni con fx_rolling: un
 * deal chiude la posizione al cambio di chiusura della coppia (BID, valuta D+2)
 * e uno la riapre sulla stessa anagrafica al cambio piu' i punti forward da D+2 a
 * D+3 (valuta D+3). Non serve un'anagrafica di forward e il settlement resta
 * quello degli altri deal.
 *
 * I deal entrano come PENDING e li esegue il processor, nell'ordine di inserimento.
 * La origin_key rende innocuo un rilancio.
 */
@Service
public class FxRollingService {

    private static final Logger log = LoggerFactory.getLogger(FxRollingService.class);

    private static final String FX_SPOT = "FSP";
    private static final int SBC_ID = 1;

    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private CounterpartyDAO counterpartyDAO;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private enum Outcome {
        ROLLED, ALREADY_ROLLED, NOTHING_TO_ROLL, PAIR_HOLIDAY
    }

    public EodTaskResult rollPositions() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate date = sbc.getOfficialDate();
        if (sbc.getStatus() != SbcStatus.CLOSING) {
            return EodTaskResult.failed(EodStep.FX_ROLLING, date, "FX rolling runs only during the end of day");
        }

        // La data ufficiale e' in cache e questo servizio non riceve il cambio data; i
        // cambi di chiusura sono caricati dopo il suo avvio
        marketDataService.refreshOfficialDate();
        marketDataService.loadSpotPrice();

        int processed = 0;
        int created = 0;
        int skipped = 0;
        int errors = 0;
        String firstError = null;
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            if (!position.isFxRolling()) {
                continue;
            }
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                try {
                    Outcome outcome = transactionTemplate.execute(status -> rollDetail(detail, position, date));
                    if (outcome == null) {
                        continue; // non e' un FX spot
                    }
                    processed++;
                    if (outcome == Outcome.ROLLED) {
                        created += 2;
                    } else {
                        skipped++;
                    }
                } catch (Exception e) {
                    processed++;
                    errors++;
                    log.error("### FX rolling error for position detail {}", detail.getIdPositionDetail(), e);
                    if (firstError == null) {
                        firstError = "position detail " + detail.getIdPositionDetail() + ": " + e.getLocalizedMessage();
                    }
                }
            }
        }
        return EodTaskResult.of(EodStep.FX_ROLLING, date, processed, created, skipped, errors,
                firstError == null ? "" : "First error: " + firstError);
    }

    private Outcome rollDetail(PositionDetail detail, PositionMasterData position, LocalDate date) {
        MasterData masterData = (MasterData) Hibernate.unproxy(masterDataDAO.findByIdMasterData(detail.getMasterData()));
        if (!FX_SPOT.equals(masterData.getAssetClass().getCode())) {
            return null;
        }
        if (PnlCalc.isFlat(detail.getBuyQty(), detail.getSellQty())) {
            return Outcome.NOTHING_TO_ROLL;
        }

        String keyPrefix = "FX_ROLL:" + detail.getIdPositionDetail() + ":" + date;
        String closeKey = keyPrefix + ":CLOSE";
        String openKey = keyPrefix + ":OPEN";
        // chiusura e riapertura nascono nella stessa transazione: basta controllarne una
        if (financialTxnDAO.existsByOriginKey(closeKey)) {
            return Outcome.ALREADY_ROLLED;
        }

        ForexMasterData forex = (ForexMasterData) masterData;
        Calendar calendar = new Calendar(forex.getCurrencyList());
        // Festivo per la coppia ma non per il sistema: lo spot di oggi ha la stessa valuta
        // di quello di ieri, e la posizione e' gia' stata portata al giorno dopo
        if (!calendar.isBusinessDay(date)) {
            log.info("FX rolling {}: {} is a holiday for the pair, not rolled", forex.getCode(), date);
            return Outcome.PAIR_HOLIDAY;
        }
        double close = marketDataService.getSpotPrice(forex.getCode(), RequestType.BID);
        if (close <= 0.) {
            throw new IllegalStateException("No closing rate for " + forex.getCode());
        }
        LocalDate closeValue = calendar.getNextBusinessDate(date, forex.getBusinessDays());
        LocalDate openValue = calendar.getNextBusinessDate(closeValue, 1);
        double open = close + forwardPoints(forex, closeValue, openValue, close);

        double net = detail.getBuyQty() - detail.getSellQty();
        TxnSide positionSide = net > 0 ? TxnSide.BUY : TxnSide.SELL;
        TxnSide closingSide = net > 0 ? TxnSide.SELL : TxnSide.BUY;
        double quantity = Math.abs(net);

        // prima la chiusura: il processor esegue i deal in ordine di id
        financialTxnDAO.saveOrUpdate(rollingDeal(detail, position, forex, date, closeValue, closingSide, quantity, close,
                "FX roll " + date + " close", closeKey));
        financialTxnDAO.saveOrUpdate(rollingDeal(detail, position, forex, date, openValue, positionSide, quantity, open,
                "FX roll " + date + " open", openKey));
        log.info("FX rolling {} position detail {}: {} {} at {} then at {}", forex.getCode(), detail.getIdPositionDetail(),
                positionSide, quantity, close, open);
        return Outcome.ROLLED;
    }

    /**
     * Punti forward tra le due valute, con le curve della coppia ricostruite ai
     * dati di chiusura.
     */
    private double forwardPoints(ForexMasterData forex, LocalDate closeValue, LocalDate openValue, double spot) {
        marketDataService.reloadCurves(List.of(forex.getCcyIrc(), forex.getBcyIrc()));
        FxForwardInput input = fxForwardCalculator.getFxForwardInput(forex.getCode(), closeValue, openValue, spot,
                marketDataService.getDiscountCurve(forex.getCcyIrc()), marketDataService.getDiscountCurve(forex.getBcyIrc()), null);
        return fxForwardCalculator.fxFwdValuation(input).forwardPoints();
    }

    private FinancialTxn rollingDeal(PositionDetail detail, PositionMasterData position, MasterData masterData, LocalDate tradeDate,
            LocalDate valueDate, TxnSide side, double quantity, double price, String description, String originKey) {
        FinancialTxn txn = new FinancialTxn();
        txn.setCounterparty(counterpartyDAO.findByIdCounterparty(detail.getCounterparty()));
        txn.setPositionMd(position);
        txn.setMasterData(masterData);
        txn.setDescription(description);
        txn.setTradeDate(Date.valueOf(tradeDate));
        txn.setValueDate(Date.valueOf(valueDate));
        txn.setSettlement(Date.valueOf(valueDate));
        txn.setTxnSide(side);
        txn.setQuantity(quantity);
        txn.setPrice(price);
        // come per gli altri FX spot: il cambio contrattuale
        txn.setFxRate(price);
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnOrigin(TxnOrigin.EOD);
        txn.setOriginKey(originKey);
        return txn;
    }
}
