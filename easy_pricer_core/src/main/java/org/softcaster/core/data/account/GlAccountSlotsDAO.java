package org.softcaster.core.data.account;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("glAccountSlotsDAO")
public class GlAccountSlotsDAO {

    private final GlAccountSlotsRepository repository;

    public GlAccountSlotsDAO(GlAccountSlotsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public GlAccountSlots findByAccountSlotId(Integer accountSlotId) {
        return repository.findByAccountSlotId(accountSlotId);
    }

    @Transactional(readOnly = true)
    public GlAccountSlots findByAccountAndCurrency(Integer account, Integer currency) {
        return repository.findByAccountAndCurrency(account, currency);
    }

    @Transactional
    public GlAccountSlots saveOrUpdate(GlAccountSlots glAccountSlots) {
        return repository.save(glAccountSlots);
    }

    @Transactional
    public void delete(GlAccountSlots glAccountSlots) {
        repository.delete(glAccountSlots);
    }
    
    @Transactional(readOnly = true)
    public List<GlAccountSlots> findAll() {
        return repository.findAll();
    }
}
