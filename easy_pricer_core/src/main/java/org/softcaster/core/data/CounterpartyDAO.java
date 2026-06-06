package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("counterpartyDAO")
public class CounterpartyDAO {

    @Resource
    private CounterpartyRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public Counterparty findByIdCounterparty(Integer idCounterparty) {
        return repository.findByIdCounterparty(idCounterparty);
    }

    @Transactional(readOnly = true)
    public List<Counterparty> findAll() {
        //return repository.findAll(sortByCode);
        return repository.findAllWithRoles(sortByCode);
    }

    @Transactional
    public Counterparty saveOrUpdate(Counterparty counterparty) {
        return repository.save(counterparty);
    }

    @Transactional
    public void delete(Counterparty counterparty) {
        repository.delete(counterparty);
    }

}
