package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntriesRepository extends JpaRepository<JournalEntries, Integer> {

    public JournalEntries findByJournalEntryId(Integer journalEntryId);
}
