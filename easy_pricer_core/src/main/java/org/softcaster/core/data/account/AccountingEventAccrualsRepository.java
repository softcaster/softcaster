package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEventAccrualsRepository extends JpaRepository<AccountingEventAccruals, Integer> {

    public AccountingEventAccruals findByEventId(Integer eventId);
}
