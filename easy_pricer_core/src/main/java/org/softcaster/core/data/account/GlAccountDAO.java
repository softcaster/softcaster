package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("glAccountDAO")
public class GlAccountDAO {

    @Resource
    private GlAccountRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public GlAccount findByAccountId(Integer accountId) {
        return repository.findByAccountId(accountId);
    }
   
    @Transactional(readOnly = true)
    public GlAccount findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<GlAccount> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public GlAccount saveOrUpdate(GlAccount glAccount) {
        return repository.save(glAccount);
    }

    @Transactional
    public void delete(GlAccount glAccount) {
        repository.delete(glAccount);
    }

}
