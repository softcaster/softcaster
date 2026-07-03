package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ledgerBalancesDAO")
public class LedgerBalancesDAO {

    @Resource
    private LedgerBalancesRepository repository;

    @Transactional(readOnly = true)
    public LedgerBalances findByLedgerBalanceId(Integer ledgerBalanceId) {
        return repository.findByLedgerBalanceId(ledgerBalanceId);
    }

    @Transactional
    public LedgerBalances saveOrUpdate(LedgerBalances ledgerBalances) {
        return repository.save(ledgerBalances);
    }

    @Transactional
    public void delete(LedgerBalances ledgerBalances) {
        repository.delete(ledgerBalances);
    }

}
