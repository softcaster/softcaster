package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountingEventsDAO")
public class AccountingEventDAO {

    @Resource
    private AccountingEventRepository repository;

    @Transactional(readOnly = true)
    public List<AccountingEvent> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AccountingEvent findByEventId(Integer eventId) {
        return repository.findByEventId(eventId);
    }

    @Transactional
    public List<AccountingEvent> findTradeEvents(EventSourceType sourceType,
            Collection<EventType> eventTypes, AccountingEventStatus eventStatus) {
        return repository.findTradeEvents(sourceType, eventTypes, eventStatus);
    }

    @Transactional
    public List<AccountingEvent> findTradeEvents(EventSourceType sourceType,
            EventType eventType, AccountingEventStatus eventStatus) {
        return repository.findTradeEvents(sourceType, eventType, eventStatus);
    }

    @Transactional
    public AccountingEvent saveOrUpdate(AccountingEvent accountingEvent) {
        return repository.save(accountingEvent);
    }

    @Transactional
    public void delete(AccountingEvent accountingEvent) {
        repository.delete(accountingEvent);
    }
}
