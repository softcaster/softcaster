package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountNatureDAO")
public class AccountNatureDAO {

    @Resource
    private AccountNatureRepository repository;

    @Transactional(readOnly = true)
    public AccountNature findByNatureId(Integer natureId) {
        return repository.findByNatureId(natureId);
    }
    
    @Transactional(readOnly = true)
    public List<AccountNature> findAll() {
        return repository.findAll();
    }

    @Transactional
    public AccountNature saveOrUpdate(AccountNature accountNature) {
        return repository.save(accountNature);
    }

    @Transactional
    public void delete(AccountNature accountNature) {
        repository.delete(accountNature);
    }
}
