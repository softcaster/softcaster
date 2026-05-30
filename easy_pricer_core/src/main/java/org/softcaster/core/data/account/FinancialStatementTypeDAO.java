package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("financialStatementTypeDAO")
public class FinancialStatementTypeDAO {

    @Resource
    private FinancialStatementTypeRepository repository;

    @Transactional(readOnly = true)
    public FinancialStatementType findByStatementTypeId(Integer statementTypeId) {
        return repository.findByStatementTypeId(statementTypeId);
    }

    @Transactional(readOnly = true)
    public List<FinancialStatementType> findAll() {
        return repository.findAll();
    }

    @Transactional
    public FinancialStatementType saveOrUpdate(FinancialStatementType financialStatementType) {
        return repository.save(financialStatementType);
    }

    @Transactional
    public void delete(FinancialStatementType financialStatementType) {
        repository.delete(financialStatementType);
    }

}
