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

    @Transactional
    public RefRateFixing saveOrUpdate(RefRateFixing refRateFixing) {
        return repository.save(refRateFixing);
    }

    @Transactional
    public void delete(RefRateFixing refRateFixing) {
        repository.delete(refRateFixing);
    }
}
