package org.softcaster.core.data;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountingStatusDAO")
public class AccountingStatusDAO {

    @Resource
    private AccountingStatusRepository repository;

    @Transactional(readOnly = true)
    public AccountingStatus findByAccountingStatusId(Integer accountingStatusId) {
        return repository.findByAccountingStatusId(accountingStatusId);
    }

    @Transactional
    public AccountingStatus saveOrUpdate(AccountingStatus accountingStatus) {
        return repository.save(accountingStatus);
    }

    @Transactional
    public void delete(AccountingStatus accountingStatus) {
        repository.delete(accountingStatus);
    }

}
