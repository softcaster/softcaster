package org.softcaster.core.data;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("refRateIndexDAO")
public class RefRateIndexDAO {

    private RefRateIndexRepository repository;

    public RefRateIndexDAO(RefRateIndexRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RefRateIndex findByRefRateIndexId(Integer refRateIndexId) {
        return repository.findByRefRateIndexId(refRateIndexId);
    }

    @Transactional
    public RefRateIndex saveOrUpdate(RefRateIndex refRateIndex) {
        return repository.save(refRateIndex);
    }

    @Transactional
    public void delete(RefRateIndex refRateIndex) {
        repository.delete(refRateIndex);
    }

    public List<RefRateIndex> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
