package org.softcaster.core.data.account;

import java.util.Collection;
import java.util.List;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Integer> {

    public AccountingEvent findByEventId(Integer eventId);

    // Query Nativa atomica che estrae e blocca SOLOLO gli ID (Risolve l'errore clazz_ e OUTER JOIN)
    @Query(value = "SELECT ae.event_id FROM accounting_events ae "
            + "WHERE ae.source_type = :#{#sourceType.id} "
            + "AND ae.event_type = :#{#eventType.id} "
            + "AND ae.event_status = :#{#eventStatus.id} "
            + "ORDER BY ae.created_at ASC "
            + "FOR UPDATE OF ae SKIP LOCKED", nativeQuery = true)
    List<Integer> findAndLockTradeEventIds(@Param("sourceType") EventSourceType sourceType,
            @Param("eventType") EventType eventType,
            @Param("eventStatus") AccountingEventStatus eventStatus);

    // Query JPQL standard per riprendere le entità polimorfiche JOINED partendo dagli ID già bloccati
    @Query("SELECT ae FROM AccountingEvent ae WHERE ae.eventId IN :ids")
    List<AccountingEvent> findAllByIds(@Param("ids") Collection<Integer> ids);

    public AccountingEvent findByEventKey(String eventKey);
}
