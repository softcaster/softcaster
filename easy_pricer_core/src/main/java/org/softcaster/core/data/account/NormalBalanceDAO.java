package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("normalBalanceDAO")
public class NormalBalanceDAO {

    @Resource
    private NormalBalanceRepository repository;

    @Transactional(readOnly = true)
    public NormalBalance findByBalanceId(Integer balanceId) {
        return repository.findByBalanceId(balanceId);
    }

    @Transactional(readOnly = true)
    public List<NormalBalance> findAll() {
        return repository.findAll();
    }

    @Transactional
    public NormalBalance saveOrUpdate(NormalBalance normalBalance) {
        return repository.save(normalBalance);
    }

    @Transactional
    public void delete(NormalBalance normalBalance) {
        repository.delete(normalBalance);
    }

}
