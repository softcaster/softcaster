package org.softcaster.core.data;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("counterpartyRoleMappingDAO")
public class CounterpartyRoleMappingDAO {

    @Resource
    private CounterpartyRoleMappingRepository repository;

    @Transactional(readOnly = true)
    public CounterpartyRoleMapping findByCounterpartyRoleMappingId(Integer counterpartyRoleMappingId) {
        return repository.findByCounterpartyRoleMappingId(counterpartyRoleMappingId);
    }

    @Transactional
    public CounterpartyRoleMapping saveOrUpdate(CounterpartyRoleMapping counterpartyRoleMapping) {
        return repository.save(counterpartyRoleMapping);
    }

    @Transactional
    public void delete(CounterpartyRoleMapping counterpartyRoleMapping) {
        repository.delete(counterpartyRoleMapping);
    }
}
