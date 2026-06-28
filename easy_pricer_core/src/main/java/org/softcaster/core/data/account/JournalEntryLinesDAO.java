package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("journalEntryLinesDAO")
public class JournalEntryLinesDAO {

    @Resource
    private JournalEntryLinesRepository repository;

    @Transactional(readOnly = true)
    public JournalEntryLines findByJournalEntryLineId(Integer journalEntryLineId) {
        return repository.findByJournalEntryLineId(journalEntryLineId);
    }

    @Transactional
    public JournalEntryLines saveOrUpdate(JournalEntryLines journalEntryLines) {
        return repository.save(journalEntryLines);
    }

    @Transactional
    public void delete(JournalEntryLines journalEntryLines) {
        repository.delete(journalEntryLines);
    }

    /**
     * Recupera le linee di giornale associate al txnId passando dal repository
     * Spring Data. La transazione readOnly ottimizza la memoria di Hibernate in
     * fase di lettura.
     *
     * @param txnId
     * @return
     */
    @Transactional(readOnly = true)
    public List<JournalEntryLines> findLinesByFinancialTxnId(Integer txnId) {
        if (txnId == null) {
            return java.util.Collections.emptyList();
        }
        return repository.findLinesByFinancialTxnId(txnId);
    }
}
