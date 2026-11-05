package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import java.util.List;
import org.softcaster.engine.enums.EventType;
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

    @Transactional(readOnly = true)
    public long countUnbalancedEntries(double tolerance) {
        return repository.countUnbalancedEntries(tolerance);
    }

    /**
     * Linee di giornale del deal: esecuzione e, se c'e' stato, settlement. Il
     * settlement le usa per eliminare il memo (le sue non esistono ancora), lo
     * storno di cancel e amend per annullare il deal per intero.
     *
     * @param txnId
     * @return
     */
    @Transactional(readOnly = true)
    public List<JournalEntryLines> findLinesByFinancialTxnId(Integer txnId) {
        if (txnId == null) {
            return java.util.Collections.emptyList();
        }
        return repository.findLinesByFinancialTxnId(txnId,
                List.of(EventType.TRADE_EXECUTED.getId(), EventType.SETTLEMENT.getId()));
    }
}
