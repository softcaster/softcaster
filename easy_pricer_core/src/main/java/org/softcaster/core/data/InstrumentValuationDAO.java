package org.softcaster.core.data;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("instrumentValuationDAO")
public class InstrumentValuationDAO {

    @Resource
    private InstrumentValuationRepository repository;

    // Iniettiamo direttamente l'EntityManager nativo di JPA
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public InstrumentValuation findByInstrumentValuationId(Integer instrumentValuationId) {
        return repository.findByInstrumentValuationId(instrumentValuationId);
    }

    @Transactional
    public InstrumentValuation saveOrUpdate(InstrumentValuation instrumentValuation) {
        if (instrumentValuation.getInstrumentValuationId() == null) {
            // Se l'ID è null, inserisce una nuova riga (INSERT)
            entityManager.persist(instrumentValuation);
            return instrumentValuation;
        } else {
            // Se l'ID è presente, sincronizza lo stato ed esegue l'aggiornamento (UPDATE)
            // Questo blocca l'avanzamento della sequenza sul database!
            return entityManager.merge(instrumentValuation);
        }
    }

    @Transactional
    public void upsertValuation(InstrumentValuation valuation) {
        // Query nativa SQL (sintassi standard PostgreSQ)
        // Se la riga con lo stesso ID esiste, fa UPDATE di tutti i valori calcolati, altrimenti fa INSERT
        String sql = """
            INSERT INTO instrument_valuation 
            (instrument_valuation_id, master_data, market_price, accrued_interest, ytm, duration, mod_duration, theoretical_price, valuation_date)
            VALUES (:id, :md, :price, :accrued, :ytm, :dur, :modDur, :theo, :vdate)
            ON CONFLICT (instrument_valuation_id) 
            DO UPDATE SET 
                market_price = EXCLUDED.market_price,
                accrued_interest = EXCLUDED.accrued_interest,
                ytm = EXCLUDED.ytm,
                duration = EXCLUDED.duration,
                mod_duration = EXCLUDED.mod_duration,
                theoretical_price = EXCLUDED.theoretical_price,
                valuation_date = EXCLUDED.valuation_date
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", valuation.getInstrumentValuationId())
            .setParameter("md", valuation.getMasterData().getIdMasterData())
            .setParameter("price", valuation.getMarketPrice())
            .setParameter("accrued", valuation.getAccruedInterest())
            .setParameter("ytm", valuation.getYtm())
            .setParameter("dur", valuation.getDuration())
            .setParameter("modDur", valuation.getModDuration())
            .setParameter("theo", valuation.getTheoreticalPrice())
            .setParameter("vdate", valuation.getValuationDate())
            .executeUpdate();
    }
    
    @Transactional
    public void delete(InstrumentValuation instrumentValuation) {
        repository.delete(instrumentValuation);
    }
}
