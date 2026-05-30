package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingStatusRepository extends JpaRepository<AccountingStatus, Integer> {

    public AccountingStatus findByAccountingStatusId(Integer accountingStatusId);
}
