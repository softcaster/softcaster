package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Integer>{

    public AccountingEvent findByEventId(Integer eventId);
}
