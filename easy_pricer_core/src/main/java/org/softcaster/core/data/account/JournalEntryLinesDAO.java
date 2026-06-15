package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
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

}
