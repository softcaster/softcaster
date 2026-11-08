package org.softcaster.core.data.account;

import java.util.Collection;
import java.util.List;
import org.softcaster.engine.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEventAccrualsRepository extends JpaRepository<AccountingEventAccruals, Integer> {

    public AccountingEventAccruals findByEventId(Integer eventId);

    public List<AccountingEventAccruals> findByPositionDetailAndEventTypeInOrderByEventId(Integer positionDetail,
            Collection<EventType> eventTypes);
}
