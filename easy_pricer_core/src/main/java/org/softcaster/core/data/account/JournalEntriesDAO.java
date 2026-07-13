package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.softcaster.core.dto.AccountDetailsBalanceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Service("journalEntriesDAO")
public class JournalEntriesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private JournalEntriesRepository repository;

    @Transactional(readOnly = true)
    public JournalEntries findByJournalEntryId(Integer journalEntryId) {
        return repository.findByJournalEntryId(journalEntryId);
    }

    @Transactional
    public JournalEntries saveOrUpdate(JournalEntries journalEntries) {
        return repository.save(journalEntries);
    }

    @Transactional
    public void delete(JournalEntries journalEntries) {
        repository.delete(journalEntries);
    }

    @Transactional(readOnly = true)
    public List<AccountDetailsBalanceDto> findBalanceWithDetailsByPositionDetails(Collection<Integer> positionDetails) {
        // Gestione di sicurezza per evitare query SQL non valide in caso di lista vuota
        if (positionDetails == null || positionDetails.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object[]> rows = repository.findBalanceWithDetailsByPositionDetailsNative(positionDetails);
        return rows.stream().map(row -> new AccountDetailsBalanceDto(
                (Integer) row[0],
                (Integer) row[1],
                (String) row[2],
                (String) row[3],
                row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO,
                row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO
        )).toList();
        /*
        String sql = """
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
            JOIN gl_accounts ga ON jel.gl_account = ga.account_id
            WHERE ae.position_detail IN (:positionDetails)
            GROUP BY ae.position_detail, ga.account_id, ga.code, ga.description
        """;

        // Crea la query nativa usando l'EntityManager del DAO
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("positionDetails", positionDetails);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        // Mappatura manuale 
        return rows.stream().map(row -> new AccountDetailsBalanceDto(
                (Integer) row[0],
                (Integer) row[1],
                (String) row[2],
                (String) row[3],
                row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO,
                row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO
        )).toList();
    */
    }
}
