package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerBalancesRepository extends JpaRepository<LedgerBalances, Integer> {

    public LedgerBalances findByLedgerBalanceId(Integer ledgerBalanceId);
}
