package org.softcaster.core.data.account;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Integer> {

    public AccountingEvent findByEventId(Integer eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // -2 attiva lo SKIP LOCKED in Hibernate
    @Query("SELECT ae FROM AccountingEvent ae "
            + "WHERE ae.sourceType = :sourceType "
            + "AND ae.eventType IN :eventTypes "
            + "AND ae.eventStatus = :eventStatus "
            + "ORDER BY ae.eventType ASC, ae.createdAt ASC")
    public List<AccountingEvent> findTradeEvents(@Param("sourceType") EventSourceType sourceType,
            @Param("eventTypes") Collection<EventType> eventTypes,
            @Param("eventStatus") AccountingEventStatus eventStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // -2 attiva lo SKIP LOCKED in Hibernate
    @Query("SELECT ae FROM AccountingEvent ae "
            + "WHERE ae.sourceType = :sourceType "
            + "AND ae.eventType = :eventType "
            + "AND ae.eventStatus = :eventStatus "
            + "ORDER BY createdAt ASC")
    public List<AccountingEvent> findTradeEvents(@Param("sourceType") EventSourceType sourceType,
            @Param("eventType") EventType eventTypes,
            @Param("eventStatus") AccountingEventStatus eventStatus);
}
