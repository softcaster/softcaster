package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("refRateIndexDAO")
public class RefRateIndexDAO {

    private RefRateIndexRepository repository;
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

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

    @Transactional(readOnly = true)
    public List<RefRateIndex> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public List<String> findNames() {
        return repository.findNames();
    }

}
