package org.softcaster.core.data.account;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountingEventsDAO")
public class AccountingEventDAO {

    private final AccountingEventRepository repository;

    public AccountingEventDAO(AccountingEventRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public List<AccountingEvent> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AccountingEvent findByEventId(Integer eventId) {
        return repository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public AccountingEvent findByEventKey(String eventKey) {
        return repository.findByEventKey(eventKey);
    }

    // Manteniamo la firma originale del metodo per non rompere altre chiamate esterne
    @Transactional
    public List<AccountingEvent> findTradeEvents(EventSourceType sourceType,
            EventType eventType, AccountingEventStatus eventStatus) {
        List<Integer> lockedIds = repository.findAndLockTradeEventIds(sourceType, eventType, eventStatus);
        if (lockedIds.isEmpty()) {
            return new ArrayList<>();
        }
        return repository.findAllByIds(lockedIds);
    }

    @Transactional
    public List<AccountingEvent> fetchAndClaimEvents(EventSourceType sourceType,
            EventType eventType) {
        
        // 1. Estrae e applica il Lock atomico SOLO sugli ID tramite SQL nativo (PostgreSQL esegue SKIP LOCKED senza impedimenti)
        List<Integer> lockedIds = repository.findAndLockTradeEventIds(
                sourceType, eventType, AccountingEventStatus.NEW);

        if (lockedIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 1b. Ricarica le entità polimorfiche complete (Hibernate valorizza correttamente la colonna clazz_)
        // Avendo già acquisito il lock al punto 1, queste righe sono blindate nella transazione corrente
        List<AccountingEvent> pendingEvents = repository.findAllByIds(lockedIds);

        List<AccountingEvent> claimedEvents = new ArrayList<>();

        // 2. Modifica lo stato e salva ogni record all'istante (Logica originale invariata)
        for (AccountingEvent event : pendingEvents) {
            event.setEventStatus(AccountingEventStatus.PROCESSING);

            // 3. Persiste il cambio di stato
            AccountingEvent updatedEvent = repository.save(event);
            claimedEvents.add(updatedEvent);
        }

        return claimedEvents; 
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
