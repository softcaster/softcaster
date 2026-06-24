/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.softcaster.core.data.InstrumentValuation;

public class ValuationContext {
    // Memorizziamo i calcoli completati
    private final Map<Integer, InstrumentValuation> valuationCache = new ConcurrentHashMap<>();
    
    // Mappa tecnica per bloccare i thread sullo stesso strumento
    private final Map<Integer, Object> locks = new ConcurrentHashMap<>();

    public Map<Integer, InstrumentValuation> getValuationCache() {
        return valuationCache;
    }

    /**
     * Esegue il calcolo in modo thread-safe. Se un altro thread sta già calcolando 
     * lo stesso strumento, il thread corrente attende il risultato senza ricalcolarlo.
     * @param masterDataId
     * @param calculationSupplier
     * @return 
     */
    public InstrumentValuation computeIfAbsentThreadSafe(Integer masterDataId, Supplier<InstrumentValuation> calculationSupplier) {
        // Se il dato è già pronto, lo restituiamo subito senza fare lock
        InstrumentValuation existing = valuationCache.get(masterDataId);
        if (existing != null) {
            return existing;
        }

        // Otteniamo o creiamo un oggetto di lock univoco per questo specifico masterDataId
        Object lock = locks.computeIfAbsent(masterDataId, k -> new Object());

        synchronized (lock) {
            // Doppia verifica (Double-Checked Locking) dentro il blocco sincronizzato
            existing = valuationCache.get(masterDataId);
            if (existing != null) {
                return existing;
            }

            // Eseguiamo il calcolo (questo blocco ora è eseguito da un solo thread alla volta per ID)
            InstrumentValuation computed = calculationSupplier.get();
            valuationCache.put(masterDataId, computed);
            
            // Pulizia opzionale del lock per non accumulare oggetti in memoria
            locks.remove(masterDataId);
            
            return computed;
        }
    }
}
