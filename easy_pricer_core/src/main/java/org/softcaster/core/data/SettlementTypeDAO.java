package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("settlementTypeDAO")
public class SettlementTypeDAO {

    @Resource
    private SettlementTypeRepository repository;

    @Transactional(readOnly = true)
    public SettlementType findByIdSettlementType(Integer idSettlementType) {
        return repository.findByIdSettlementType(idSettlementType);
    }

    @Transactional
    public SettlementType saveOrUpdate(SettlementType settlementType) {
        return repository.save(settlementType);
    }

    @Transactional
    public void delete(SettlementType settlementType) {
        repository.delete(settlementType);
    }

    @Transactional(readOnly = true)
    public List<SettlementType> findAll() {
        return repository.findAll();
    }
}
