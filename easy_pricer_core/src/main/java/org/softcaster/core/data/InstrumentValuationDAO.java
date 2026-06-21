package org.softcaster.core.data;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("instrumentValuationDAO")
public class InstrumentValuationDAO {

    @Resource
    private InstrumentValuationRepository repository;

    @Transactional(readOnly = true)
    public InstrumentValuation findByInstrumentValuationId(Integer instrumentValuationId) {
        return repository.findByInstrumentValuationId(instrumentValuationId);
    }

    @Transactional
    public InstrumentValuation saveOrUpdate(InstrumentValuation instrumentValuation) {
        return repository.save(instrumentValuation);
    }

    @Transactional
    public void delete(InstrumentValuation instrumentValuation) {
        repository.delete(instrumentValuation);
    }
}
