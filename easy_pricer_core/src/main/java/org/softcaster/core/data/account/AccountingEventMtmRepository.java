package org.softcaster.core.data.account;

import java.time.LocalDate;
import java.util.Optional;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEventMtmRepository extends JpaRepository<AccountingEventMtm, Integer> {

    public AccountingEventMtm findByEventId(Integer eventId);

    public Optional<AccountingEventMtm> findFirstByPositionDetailAndValuationDateBeforeAndEventStatusOrderByValuationDateDesc(
            Integer positionDetail, LocalDate valuationDate, AccountingEventStatus eventStatus);

    public Optional<AccountingEventMtm> findFirstByPositionDetailAndValuationDateBeforeOrderByValuationDateDesc(
            Integer positionDetail, LocalDate valuationDate);
}
