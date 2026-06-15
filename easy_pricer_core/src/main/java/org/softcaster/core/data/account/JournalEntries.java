package org.softcaster.core.data.account;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.JournalEntryTypeConverter;
import org.softcaster.engine.enums.JournalEntryType;

@Entity
@Table(name = "journal_entries")
@SuppressWarnings("PersistenceUnitPresent")
public class JournalEntries implements Serializable {

    @Id
    @SequenceGenerator(name = "journal_entries_seq", sequenceName = "journal_entries_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_entries_seq")
    @Column(name = "journal_entry_id")
    private Integer journalEntryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_event")
    private AccountingEvent accountingEvent;

    @Convert(converter = JournalEntryTypeConverter.class)
    @Column(name = "entry_type")
    private JournalEntryType entryType;

    // Le JournalEntryLines non dovrebbero mai essere cancellate solo
    // movimenti di senso contrario per questo uso PERSIST e tolto
    // orphanRemoval = true
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "journal_entry", nullable = false) // FK in child table journal_entry_lines
    private final List<JournalEntryLines> jeLines = new ArrayList<>();

    public void addLine(JournalEntryLines line) {
        line.setLineNo(jeLines.size() + 1);
        jeLines.add(line);
    }
    
    @Column(name = "business_date")
    private java.sql.Date businessDate;

    @Column(name = "reference")
    private String reference;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversal_of")
    private JournalEntries reversalOf;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Integer getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Integer journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public AccountingEvent getAccountingEvent() {
        return accountingEvent;
    }

    public void setAccountingEvent(AccountingEvent accountingEvent) {
        this.accountingEvent = accountingEvent;
    }

    public JournalEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(JournalEntryType entryType) {
        this.entryType = entryType;
    }

    public java.sql.Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(java.sql.Date businessDate) {
        this.businessDate = businessDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JournalEntries getReversalOf() {
        return reversalOf;
    }

    public void setReversalOf(JournalEntries reversalOf) {
        this.reversalOf = reversalOf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JournalEntries that)) {
            return false;
        }

        return journalEntryId != null
                && journalEntryId.equals(that.journalEntryId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the jeLines
     */
    public List<JournalEntryLines> getJeLines() {
        return jeLines;
    }
}
