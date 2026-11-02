package org.softcaster.core.data.account;

import java.time.LocalDate;
import java.util.Optional;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fotografie MTM di fine giornata (accounting_event_mtm).
 */
@Service("accountingEventMtmDAO")
public class AccountingEventMtmDAO {

    private final AccountingEventMtmRepository repository;

    public AccountingEventMtmDAO(AccountingEventMtmRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AccountingEventMtm findByEventId(Integer eventId) {
        return repository.findByEventId(eventId);
    }

    /**
     * Ultima fotografia gia' contabilizzata (PROCESSED) della posizione prima
     * della data: base della variazione da registrare.
     */
    @Transactional(readOnly = true)
    public Optional<AccountingEventMtm> findLastPosted(Integer positionDetail, LocalDate before) {
        return repository.findFirstByPositionDetailAndValuationDateBeforeAndEventStatusOrderByValuationDateDesc(
                positionDetail, before, AccountingEventStatus.PROCESSED);
    }

    /**
     * Ultima fotografia della posizione prima della data, in qualunque stato.
     */
    @Transactional(readOnly = true)
    public Optional<AccountingEventMtm> findLast(Integer positionDetail, LocalDate before) {
        return repository.findFirstByPositionDetailAndValuationDateBeforeOrderByValuationDateDesc(positionDetail, before);
    }

    @Transactional
    public AccountingEventMtm saveOrUpdate(AccountingEventMtm event) {
        return repository.save(event);
    }
}
