package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryLinesRepository extends JpaRepository<JournalEntryLines,Integer>{
	public JournalEntryLines findByJournalEntryLineId(Integer journalEntryLineId);
}
