package org.softcaster.core.data.account;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("journalEntriesDAO")
public class JournalEntriesDAO {

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

}
