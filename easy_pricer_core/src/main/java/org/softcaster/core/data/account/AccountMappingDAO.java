package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountMappingDAO")
public class AccountMappingDAO {

    @Resource
    private AccountMappingRepository repository;

    @Transactional(readOnly = true)
    public AccountMapping findByAccountMappingId(Integer accountMappingId) {
        return repository.findByAccountMappingId(accountMappingId);
    }
    
    @Transactional(readOnly = true)
    public List<AccountMapping> findAll() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<AccountMapping> findAllWithAssociations() {
        return repository.findAllWithAssociations();
    }

    @Transactional(readOnly = true)
    public String findAccount(String mappingKey, int currencyId) {
        return repository.findAccount(mappingKey, currencyId);
    }    

    public AccountMapping saveOrUpdate(AccountMapping accountMapping) {
        return repository.save(accountMapping);
    }

    @Transactional
    public void delete(AccountMapping accountMapping) {
        repository.delete(accountMapping);
    }

}
