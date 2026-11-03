package org.softcaster.easy_pricer_mtm.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.core.data.account.AccountingEventMtm;
import org.softcaster.core.data.account.AccountingEventMtmDAO;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fotografia di fine giornata di una posizione (accounting_event_mtm) per la
 * contabilita': un evento MTM per position_detail e data ufficiale, scritto
 * dopo la valutazione di tutte le posizioni (MtMPollingJob.executeEod).
 *
 * Nessun evento per una posizione chiusa il cui risultato (realizzato + non
 * realizzato) non e' cambiato dall'ultima fotografia. Rilancio nella stessa
 * data: la fotografia si aggiorna finche' l'evento non e' contabilizzato;
 * dopo resta com'e' (una correzione richiede storno e nuova scrittura).
 */
@Service
public class EodMtmService {

    private static final Logger log = LoggerFactory.getLogger(EodMtmService.class);

    // sotto questa soglia il risultato di una posizione chiusa e' invariato
    private static final double AMOUNT_EPS = 0.005;

    public enum Outcome {
        CREATED, UPDATED, ALREADY_POSTED, UNCHANGED, NOT_ACCOUNTED
    }

    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private AccountingEventDAO accountingEventDAO;
    @Autowired
    private AccountingEventMtmDAO accountingEventMtmDAO;

    /**
     * @param assetClasses asset class contabilizzate a MTM (le altre: NOT_ACCOUNTED)
     */
    @Transactional
    public Outcome recordSnapshot(Integer positionDetailId, LocalDate valuationDate, Set<String> assetClasses) {
        PositionDetail p = positionDetailDAO.findByIdPositionDetail(positionDetailId);
        if (p == null) {
            throw new MtmException("Position detail not found: " + positionDetailId);
        }
        MasterData md = masterDataDAO.findByIdMasterData(p.getMasterData());
        if (!assetClasses.contains(md.getAssetClass().getCode())) {
            return Outcome.NOT_ACCOUNTED;
        }

        double buyQty = nz(p.getBuyQty());
        double sellQty = nz(p.getSellQty());
        boolean flat = PnlCalc.isFlat(buyQty, sellQty);
        double realized = nz(p.getRealizedPnl());
        double unrealized = flat ? 0. : nz(p.getUnrealizedPnl());

        String eventKey = md.getCode() + " [" + positionDetailId + "] [" + EventType.MTM.getCode() + "]" + valuationDate;
        AccountingEvent existing = accountingEventDAO.findByEventKey(eventKey);
        AccountingEventMtm event;
        Outcome outcome;
        if (existing != null) {
            if (!(existing instanceof AccountingEventMtm mtm)) {
                throw new MtmException("Event " + eventKey + " is not an MTM snapshot");
            }
            if (mtm.getEventStatus() == AccountingEventStatus.PROCESSED
                    || mtm.getEventStatus() == AccountingEventStatus.PROCESSING) {
                log.warn("MTM snapshot {} already {}: not updated", eventKey, mtm.getEventStatus());
                return Outcome.ALREADY_POSTED;
            }
            event = mtm;
            outcome = Outcome.UPDATED;
        } else {
            double previousTotal = accountingEventMtmDAO.findLast(positionDetailId, valuationDate)
                    .map(AccountingEventMtm::totalPnl).orElse(0.);
            if (flat && Math.abs(realized + unrealized - previousTotal) < AMOUNT_EPS) {
                return Outcome.UNCHANGED;
            }
            event = new AccountingEventMtm();
            event.setEventType(EventType.MTM);
            event.setSourceType(EventSourceType.POSITION_DETAIL);
            event.setSourceId(positionDetailId);
            event.setPositionDetail(positionDetailId);
            event.setEventKey(eventKey);
            event.setGeneratedBy(md.getIdMasterData());
            event.setGeneratedRef("EOD");
            outcome = Outcome.CREATED;
        }

        event.setEventStatus(AccountingEventStatus.NEW);
        event.setCreatedAt(LocalDateTime.now());
        event.setValuationDate(valuationDate);
        event.setMarketPrice(nz(p.getMarketPrice()));
        event.setAccountingNominal(buyQty - sellQty);
        event.setAveragePrice(averagePrice(p, buyQty, sellQty, flat));
        event.setRealizedPnl(realized);
        event.setUnrealizedPnl(unrealized);
        accountingEventMtmDAO.saveOrUpdate(event);
        return outcome;
    }

    /**
     * Prezzo medio unitario del lato che compone la quantita' netta, come
     * PnlCalc.unrealized.
     */
    private static double averagePrice(PositionDetail p, double buyQty, double sellQty, boolean flat) {
        if (flat) {
            return 0.;
        }
        return buyQty > sellQty ? nz(p.getNotionalValueBuy()) / buyQty : nz(p.getNotionalValueSell()) / sellQty;
    }

    private static double nz(Double value) {
        return value == null ? 0. : value;
    }
}
