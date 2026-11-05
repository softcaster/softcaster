package org.softcaster.core.data.account;

import java.util.Collection;
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
        WHERE ae.source_id = :txnId and ae.event_type IN (:eventTypes)
        ORDER BY ae.event_id ASC, jel.line_no ASC
    """, nativeQuery = true)
    List<JournalEntryLines> findLinesByFinancialTxnId(@Param("txnId") Integer txnId, @Param("eventTypes") Collection<Integer> eventTypes);

    // Quadratura per registrazione e divisa, come BaseAccountingEventService in scrittura
    @Query(value = """
        SELECT count(*) FROM (
            SELECT jel.journal_entry, jel.currency
            FROM journal_entry_lines jel
            GROUP BY jel.journal_entry, jel.currency
            HAVING abs(coalesce(sum(jel.debit_amount), 0) - coalesce(sum(jel.credit_amount), 0)) > :tolerance
        ) unbalanced
    """, nativeQuery = true)
    long countUnbalancedEntries(@Param("tolerance") double tolerance);
}
