package org.softcaster.easy_pricer_proc.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Rolling FX di un deal cancellato o sostituito da un amend: ogni rolling che
 * lo comprendeva (FxRollingService in easy_pricer_lc: chiusura e riapertura
 * della posizione a ogni chiusura di giornata) si corregge della quota del
 * deal, come se il deal non fosse mai esistito (o fosse sempre stato quello
 * dell'amend).
 *
 * Un rolling della data D comprende il deal se il deal era gia' registrato
 * quando la chiusura D ha creato i deal di rolling: id del deal minore di
 * quello del deal di chiusura originale (origin_key FX_ROLL:detail:D:CLOSE).
 *
 * I due deal vivi del rolling si cancellano e, se resta una quantita', si
 * ricreano con la quantita' corretta, agli stessi cambi, date e valute
 * (origin_key con suffisso :R<deal>). Li elabora il processor come gli altri.
 */
@Component
public class FxRollingAdjustment {

    private static final Logger log = LoggerFactory.getLogger(FxRollingAdjustment.class);

    private static final String KEY = "FX_ROLL:";
    private static final String FX_SPOT = "FSP";
    private static final double EPS = 1e-6;

    @Autowired
    private FinancialTxnDAO financialTxnDAO;

    /**
     * @param txn             deal dell'utente cancellato (CANCELLED) o sostituito (AMENDED)
     * @param positionDetailId posizione del deal
     * @return rolling corretti
     */
    public int adjust(FinancialTxn txn, Integer positionDetailId, TxnStatus status) {
        if (txn.getTxnOrigin() == TxnOrigin.EOD || !FX_SPOT.equals(txn.getMasterData().getAssetClass().getCode())) {
            return 0;
        }
        double delta = -signed(txn);
        if (status == TxnStatus.AMENDED) {
            for (FinancialTxn replacement : financialTxnDAO.findReplacements(txn.getIdFinancialTxn())) {
                if (samePosition(txn, replacement)) {
                    delta += signed(replacement);
                }
            }
        }
        if (Math.abs(delta) < EPS) {
            return 0;
        }

        // rolling della posizione per data: deal originali e deal vivi
        Map<String, List<FinancialTxn>> byDate = new TreeMap<>();
        for (FinancialTxn roll : financialTxnDAO.findByOriginKeyPrefix(KEY + positionDetailId + ":")) {
            String[] parts = roll.getOriginKey().split(":");
            if (parts.length >= 4) {
                byDate.computeIfAbsent(parts[2], d -> new ArrayList<>()).add(roll);
            }
        }

        int adjusted = 0;
        for (Map.Entry<String, List<FinancialTxn>> entry : byDate.entrySet()) {
            String base = KEY + positionDetailId + ":" + entry.getKey();
            FinancialTxn originalClose = find(entry.getValue(), base + ":CLOSE", false);
            if (originalClose == null || txn.getIdFinancialTxn() > originalClose.getIdFinancialTxn()) {
                continue; // deal registrato dopo questo rolling
            }
            FinancialTxn close = findLive(entry.getValue(), ":CLOSE");
            FinancialTxn open = findLive(entry.getValue(), ":OPEN");
            if (close == null || open == null) {
                continue;
            }
            // posizione rollata con segno: la riapertura ha il lato della posizione
            double rolled = signed(open) + delta;
            cancel(close);
            cancel(open);
            if (Math.abs(rolled) > EPS) {
                String suffix = ":R" + txn.getIdFinancialTxn();
                TxnSide positionSide = rolled > 0 ? TxnSide.BUY : TxnSide.SELL;
                TxnSide closingSide = rolled > 0 ? TxnSide.SELL : TxnSide.BUY;
                // prima la chiusura: il processor esegue i deal in ordine di id
                financialTxnDAO.saveOrUpdate(copy(close, closingSide, Math.abs(rolled), base + ":CLOSE" + suffix));
                financialTxnDAO.saveOrUpdate(copy(open, positionSide, Math.abs(rolled), base + ":OPEN" + suffix));
            }
            adjusted++;
            log.info("FX rolling {} of position detail {} adjusted by {} for txn {}: rolled {}", entry.getKey(),
                    positionDetailId, delta, txn.getIdFinancialTxn(), rolled);
        }
        return adjusted;
    }

    private static boolean samePosition(FinancialTxn a, FinancialTxn b) {
        return a.getMasterData().getIdMasterData().equals(b.getMasterData().getIdMasterData())
                && a.getPositionMd().getIdPosition().equals(b.getPositionMd().getIdPosition())
                && a.getCounterparty().getIdCounterparty().equals(b.getCounterparty().getIdCounterparty());
    }

    private static double signed(FinancialTxn txn) {
        return txn.getTxnSide() == TxnSide.SELL ? -txn.getQuantity() : txn.getQuantity();
    }

    private static FinancialTxn find(List<FinancialTxn> deals, String key, boolean liveOnly) {
        for (FinancialTxn deal : deals) {
            if (deal.getOriginKey().equals(key) && (!liveOnly || isLive(deal))) {
                return deal;
            }
        }
        return null;
    }

    /**
     * Ultimo deal vivo della gamba (originale o sostituto di una correzione precedente).
     */
    private static FinancialTxn findLive(List<FinancialTxn> deals, String leg) {
        FinancialTxn live = null;
        for (FinancialTxn deal : deals) {
            String key = deal.getOriginKey();
            boolean isLeg = key.endsWith(leg) || key.contains(leg + ":");
            if (isLeg && isLive(deal) && (live == null || deal.getIdFinancialTxn() > live.getIdFinancialTxn())) {
                live = deal;
            }
        }
        return live;
    }

    private static boolean isLive(FinancialTxn deal) {
        return deal.getTxnStatus() == TxnStatus.EXECUTED || deal.getTxnStatus() == TxnStatus.PENDING;
    }

    private void cancel(FinancialTxn deal) {
        deal.setTxnStatus(TxnStatus.TO_CANCEL);
        deal.setTxnStatusPreElab(TxnStatus.TO_CANCEL);
        financialTxnDAO.saveOrUpdate(deal);
    }

    private static FinancialTxn copy(FinancialTxn source, TxnSide side, double quantity, String originKey) {
        FinancialTxn txn = new FinancialTxn();
        txn.setCounterparty(source.getCounterparty());
        txn.setPositionMd(source.getPositionMd());
        txn.setMasterData(source.getMasterData());
        txn.setDescription(source.getDescription());
        txn.setTradeDate(source.getTradeDate());
        txn.setValueDate(source.getValueDate());
        txn.setSettlement(source.getSettlement());
        txn.setTxnSide(side);
        txn.setQuantity(quantity);
        txn.setPrice(source.getPrice());
        txn.setFxRate(source.getFxRate());
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnOrigin(TxnOrigin.EOD);
        txn.setOriginKey(originKey);
        return txn;
    }
}
