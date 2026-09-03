package org.softcaster.core.data.account;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEntriesRepository extends JpaRepository<JournalEntries, Integer> {

    public JournalEntries findByJournalEntryId(Integer journalEntryId);

    @Query(value = """
    SELECT 
        ae.position_detail AS positionDetail,
        ga.account_id AS accountId,
        ga.code AS code,
        ga.description AS description,
        SUM(COALESCE(jel.debit_amount, 0.0)) AS totalDebit,
        SUM(COALESCE(jel.credit_amount, 0.0)) AS totalCredit
    FROM journal_entries je
    JOIN accounting_events ae ON je.accounting_event = ae.event_id
    JOIN journal_entry_lines jel ON jel.journal_entry = je.journal_entry_id
    JOIN gl_account_slots gas ON jel.account_slot = gas.account_slot_id
    JOIN gl_accounts ga ON gas.account = ga.account_id
    WHERE ae.position_detail IN :positionDetails
    GROUP BY ae.position_detail, ga.account_id, ga.code, ga.description
    ORDER BY ga.code
    """,
            nativeQuery = true)
    List<Object[]> findBalanceWithDetailsByPositionDetailsNative(
            @Param("positionDetails") Collection<Integer> positionDetails
    );
}
