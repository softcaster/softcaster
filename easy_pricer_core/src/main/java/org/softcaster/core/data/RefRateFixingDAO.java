package org.softcaster.core.data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("refRateFixingDAO")
public class RefRateFixingDAO {

    private RefRateFixingRepository repository;

    public RefRateFixingDAO(RefRateFixingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RefRateFixing findByRefRateFixingId(Integer refRateFixingId) {
        return repository.findByRefRateFixingId(refRateFixingId);
    }

    /**
     * Fixing dell'indice alla data, null se non caricato.
     */
    @Transactional(readOnly = true)
    public RefRateFixing findFixing(Integer refRateIndexId, java.time.LocalDate fixingDate) {
        return repository.findByRefRateIndexAndFixingDate(refRateIndexId, java.sql.Date.valueOf(fixingDate));
    }

    @Transactional
    public RefRateFixing saveOrUpdate(RefRateFixing refRateFixing) {
        return repository.save(refRateFixing);
    }

    @Transactional
    public void delete(RefRateFixing refRateFixing) {
        repository.delete(refRateFixing);
    }
}
