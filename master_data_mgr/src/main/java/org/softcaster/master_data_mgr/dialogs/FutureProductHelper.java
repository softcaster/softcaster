package org.softcaster.master_data_mgr.dialogs;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.softcaster.core.data.FutureMasterData;
import org.softcaster.core.data.FutureProduct;
import org.softcaster.core.data.FutureProductDAO;
import org.softcaster.engine.enums.SettlementType;

/**
 * Testata (FutureProduct) di una scadenza inserita dalle finestre dei future.
 *
 * Il campo "codice contratto" delle finestre e' il codice della testata
 * (MBTP, M6E): se la testata non esiste viene creata, altrimenti se ne
 * aggiornano i dati di prodotto (valore del contratto, tick, margini,
 * regolamento, sottostante), che valgono per tutte le sue scadenze.
 */
final class FutureProductHelper {

    private FutureProductHelper() {
    }

    /**
     * Dati di prodotto comuni a tutte le finestre.
     */
    record ProductFields(String code, String description, SettlementType settlementType,
            Double contractValue, Double tickSize, Double initialMargin, Double maintenanceMargin) {
    }

    /**
     * Trova o crea la testata, ne aggiorna i dati di prodotto e la salva.
     *
     * @param <P>
     * @param dao
     * @param type tipo di testata atteso
     * @param factory nuova testata del tipo atteso
     * @param fields dati di prodotto dalla finestra
     * @param expiry scadenza, gia' con i valori di default (valuta, asset
     * class, giorni di regolamento, multiplier) da usare per una testata nuova
     * @param specific dati specifici del tipo (sottostante, tipo commodity,
     * profilo di carico)
     * @return la testata salvata
     * @throws IllegalArgumentException se il codice e' vuoto o appartiene a
     * una testata di altro tipo
     */
    static <P extends FutureProduct> P resolve(FutureProductDAO dao, Class<P> type, Supplier<P> factory,
            ProductFields fields, FutureMasterData expiry, Consumer<P> specific) {
        String code = fields.code() == null ? "" : fields.code().trim();
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Codice della testata (codice contratto) mancante");
        }
        if (dao.findByCode(code).isPresent() && dao.findByCode(code, type).isEmpty()) {
            throw new IllegalArgumentException("La testata " + code + " esiste gia' con un altro tipo di future");
        }

        P product = dao.findByCode(code, type).orElseGet(() -> {
            P created = factory.get();
            created.setCode(code);
            created.setDescription(fields.description() == null || fields.description().isBlank()
                    ? code : fields.description());
            created.setExchangeContractCode(code);
            created.setCurrency(expiry.getCurrency());
            created.setAssetClass(expiry.getAssetClass());
            created.setBusinessDays(expiry.getBusinessDays());
            created.setMultiplier(expiry.getMultiplier());
            return created;
        });

        product.setSettlementType(fields.settlementType());
        product.setContractValue(fields.contractValue());
        product.setTickSize(fields.tickSize());
        product.setInitialMargin(fields.initialMargin());
        product.setMaintenanceMargin(fields.maintenanceMargin());
        specific.accept(product);
        return dao.saveOrUpdate(product);
    }
}
