package org.softcaster.core.data.account;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntryLinesRepository extends JpaRepository<JournalEntryLines, Integer> {

    public JournalEntryLines findByJournalEntryLineId(Integer journalEntryLineId);

    @Query(value = """
        SELECT jel.* 
        FROM journal_entry_lines jel
        JOIN journal_entries je ON jel.journal_entry = je.journal_entry_id
        JOIN accounting_events ae ON je.accounting_event = ae.event_id
        WHERE ae.source_id = :txnId and ae.event_type=1
        ORDER BY jel.line_no ASC
    """, nativeQuery = true)
    List<JournalEntryLines> findLinesByFinancialTxnId(@Param("txnId") Integer txnId);
}
