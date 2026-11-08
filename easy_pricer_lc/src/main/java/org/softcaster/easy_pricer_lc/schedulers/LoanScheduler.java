package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Hibernate;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Ratei e interessi delle rate dei mutui (XRM, FRM) dal piano salvato
 * (cash_flow_item: amount = quota capitale, interest = interessi della rata,
 * sul capitale intero del contratto).
 *
 * La posizione detiene una quota del mutuo: nominale / debito residuo. I
 * rimborsi delle rate sono deal EOD (LoanRepaymentService) che riducono il
 * nominale insieme al residuo, per cui la quota resta la stessa.
 *
 * ACCRUAL in (from, to]: interessi di ogni rata in proporzione ai giorni del
 * periodo che cadono nell'intervallo (lineare: a fine periodo il rateo e'
 * esattamente l'interesse della rata), per la quota regolata (senza i deal
 * ancora in memo).
 *
 * COUPON per le rate che scadono in (from, to]: interessi della rata per la
 * quota regolata prima della scadenza. L'importo ha il segno della posizione.
 */
public abstract class LoanScheduler implements IScheduler {

    @Autowired
    private PositionTxnLinksDAO positionTxnLinksDAO;

    @Override
    public List<AccountingEvent> getAccountingEvents(EventType eventType, PositionDetail detail, MasterData masterData,
            LocalDate from, LocalDate to) {
        if (!(Hibernate.unproxy(masterData) instanceof LoanMasterData lmd)) {
            return null;
        }
        return switch (eventType) {
            case ACCRUAL -> accrual(detail, lmd, from, to);
            case COUPON -> installments(detail, lmd, from, to);
            default -> null;
        };
    }

    private List<AccountingEvent> accrual(PositionDetail detail, LoanMasterData lmd, LocalDate from, LocalDate to) {
        double memo = positionTxnLinksDAO.sumQuantityByPhase(detail.getIdPositionDetail(), AccountingPhase.MEMO_POSTED);
        double nominal = detail.getBuyQty() - detail.getSellQty() - memo;
        double outstanding = LoanCalculator.outstandingAfter(lmd, from);
        if (NumberUtils.isZero(nominal) || outstanding <= 0.) {
            return null;
        }
        double amount = LoanCalculator.interestBetween(lmd, from, to) * nominal / outstanding;
        if (NumberUtils.isZero(amount)) {
            return null;
        }

        AccountingEventAccruals event = event(detail, lmd, EventType.ACCRUAL, amount, nominal,
                (int) ChronoUnit.DAYS.between(from, to), "[" + EventType.ACCRUAL.getCode() + "]" + from + "/" + to);
        List<AccountingEvent> events = new ArrayList<>();
        events.add(event);
        return events;
    }

    private List<AccountingEvent> installments(PositionDetail detail, LoanMasterData lmd, LocalDate from, LocalDate to) {
        List<AccountingEvent> events = new ArrayList<>();
        for (CashFlowItem item : LoanCalculator.installmentsIn(lmd, from, to)) {
            LocalDate dueDate = item.getEnddate().toLocalDate();
            double outstanding = LoanCalculator.outstandingBefore(lmd, dueDate);
            double settled = positionTxnLinksDAO.sumQuantitySettledBefore(detail.getIdPositionDetail(), dueDate);
            if (NumberUtils.isZero(settled) || outstanding <= 0.) {
                continue;
            }
            double amount = item.getInterest() * settled / outstanding;
            if (NumberUtils.isZero(amount)) {
                continue;
            }
            events.add(event(detail, lmd, EventType.COUPON, amount, settled,
                    (int) ChronoUnit.DAYS.between(item.getStartDate().toLocalDate(), dueDate),
                    "[" + EventType.COUPON.getCode() + "]" + dueDate));
        }
        return events;
    }

    private static AccountingEventAccruals event(PositionDetail detail, LoanMasterData lmd, EventType type, double amount,
            double nominal, int days, String keySuffix) {
        AccountingEventAccruals event = new AccountingEventAccruals();
        event.setAccrualAmount(amount);
        event.setAccountingNominal(nominal);
        event.setCouponRate(lmd.getInterestRate());
        event.setDaycount(lmd.getAccrualDaycount());
        event.setDays(days);
        event.setCreatedAt(LocalDateTime.now());
        event.setEventStatus(AccountingEventStatus.NEW);
        event.setEventType(type);
        event.setPositionDetail(detail.getIdPositionDetail());
        event.setSourceId(detail.getIdPositionDetail());
        event.setSourceType(EventSourceType.INSTRUMENT);
        event.setEventKey(lmd.getCode() + " [" + detail.getIdPositionDetail() + "] " + keySuffix);
        event.setGeneratedBy(lmd.getIdMasterData());
        event.setGeneratedRef(lmd.getCode());
        return event;
    }

    /**
     * Mutuo a tasso fisso.
     */
    @org.springframework.stereotype.Component("XRM")
    public static class FixedRateLoan extends LoanScheduler {
    }

    /**
     * Mutuo a tasso variabile: interessi delle rate come da piano salvato
     * (revisioni del tasso non gestite).
     */
    @org.springframework.stereotype.Component("FRM")
    public static class FloatingRateLoan extends LoanScheduler {
    }
}
