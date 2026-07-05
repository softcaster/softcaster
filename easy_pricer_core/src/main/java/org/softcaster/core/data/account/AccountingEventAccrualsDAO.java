package org.softcaster.core.data.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountingEventAccrualsDAO")
public class AccountingEventAccrualsDAO {

    private final AccountingEventAccrualsRepository repository;

    // Iniezione tramite costruttore (Best Practice per Spring)
    public AccountingEventAccrualsDAO(AccountingEventAccrualsRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public AccountingEventAccruals findByEventId(Integer eventId) {
        return repository.findByEventId(eventId);
    }

    @Transactional
    public AccountingEventAccruals saveOrUpdate(AccountingEventAccruals accountingEventAccruals) {
        return repository.save(accountingEventAccruals);
    }

    @Transactional
    public void delete(AccountingEventAccruals accountingEventAccruals) {
        repository.delete(accountingEventAccruals);
    }

}
