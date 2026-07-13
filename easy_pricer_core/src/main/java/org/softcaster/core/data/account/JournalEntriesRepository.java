package org.softcaster.core.data.account;

import java.util.List;
import org.softcaster.core.dto.AccountDetailsBalanceDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntriesRepository extends JpaRepository<JournalEntries, Integer> {

    public JournalEntries findByJournalEntryId(Integer journalEntryId);

    @Query("""
        SELECT new org.softcaster.core.dto.AccountDetailsBalanceDto(
            ga.accountId,
            ga.code,
            ga.description,
            SUM(COALESCE(jel.debitAmount, 0.0)),
            SUM(COALESCE(jel.creditAmount, 0.0))
        )
        FROM JournalEntries je
        JOIN je.accountingEvent ae
        JOIN JournalEntryLines jel ON jel.journalEntry = je.journalEntryId
        JOIN GlAccount ga ON jel.glAccount = ga.accountId
        WHERE ae.positionDetail = :positionDetail
        GROUP BY ga.accountId, ga.code, ga.description ORDER BY ga.code
    """)
    List<AccountDetailsBalanceDto> findBalanceWithDetailsByPositionDetail(
            @Param("positionDetail") Integer positionDetail);
}
