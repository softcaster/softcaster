package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("financialTxnDAO")
public class FinancialTxnDAO {

    @Resource
    private FinancialTxnRepository repository;

    @Transactional(readOnly = true)
    public FinancialTxn findByIdFinancialTxn(Integer idFinancialTxn) {
        return repository.findByIdFinancialTxn(idFinancialTxn);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findByTxnStatusCode(String code) {
        return repository.findByTxnStatusCode(code);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByDaycountCode(String code) {
        return repository.findAllByDaycountCode(code);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    @Transactional
    public FinancialTxn saveOrUpdate(FinancialTxn financialTxn) {
        return repository.save(financialTxn);
    }

    @Transactional
    public void delete(FinancialTxn financialTxn) {
        repository.delete(financialTxn);
    }

}
