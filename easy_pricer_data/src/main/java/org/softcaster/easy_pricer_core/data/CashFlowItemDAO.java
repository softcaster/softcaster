package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cashFlowItemDAO")
public class CashFlowItemDAO {

    @Resource
    private CashFlowItemRepository repository;

    private final Sort sortByEndDate = Sort.by(Sort.Direction.ASC, "endDate");
    
    @Transactional(readOnly = true)
    public CashFlowItem findByIdCashFlowItem(Integer idCashFlowItem) {
        return repository.findByIdCashFlowItem(idCashFlowItem);
    }

    @Transactional
    public CashFlowItem saveOrUpdate(CashFlowItem cashFlowItem) {
        return repository.save(cashFlowItem);
    }

    @Transactional
    public void delete(CashFlowItem cashFlowItem) {
        repository.delete(cashFlowItem);
    }

    @Transactional(readOnly = true)
    public List<CashFlowItem> findAll() {
        return repository.findAll(sortByEndDate);
    }

}
