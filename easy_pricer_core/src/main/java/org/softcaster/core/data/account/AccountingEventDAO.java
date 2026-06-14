package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.ArrayList;
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
    public List<AccountingEvent> fetchAndClaimEvents(EventSourceType sourceType,
            EventType eventType) {
        // 1. Legge dal DB applicando il Lock
        List<AccountingEvent> pendingEvents = repository.findTradeEvents(
                sourceType, eventType, AccountingEventStatus.NEW); // Lo stato NEW è fisso qui!

        List<AccountingEvent> claimedEvents = new ArrayList<>();

        // 2. Modifica lo stato e salva ogni record all'istante
        for (AccountingEvent event : pendingEvents) {
            event.setEventStatus(AccountingEventStatus.PROCESSING);

            // 3. Usa il metodo saveOrUpdate per persistere il cambio di stato
            AccountingEvent updatedEvent = repository.save(event);
            claimedEvents.add(updatedEvent);
        }

        return claimedEvents; // Ritorna la lista pronta per essere inviata al TaskExecutor
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
