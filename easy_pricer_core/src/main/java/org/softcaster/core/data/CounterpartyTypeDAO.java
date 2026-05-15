package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("counterpartyTypeDAO")
public class CounterpartyTypeDAO {

    @Resource
    private CounterpartyTypeRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public CounterpartyType findByIdCounterpartyType(Integer idCounterpartyType) {
        return repository.findByIdCounterpartyType(idCounterpartyType);
    }
    
    @Transactional(readOnly = true)
    public CounterpartyType findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<CounterpartyType> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public CounterpartyType saveOrUpdate(CounterpartyType counterpartyType) {
        return repository.save(counterpartyType);
    }

    @Transactional
    public void delete(CounterpartyType counterpartyType) {
        repository.delete(counterpartyType);
    }

}
