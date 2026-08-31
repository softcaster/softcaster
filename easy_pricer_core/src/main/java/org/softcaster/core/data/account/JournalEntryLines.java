package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "journal_entry_lines")
@SuppressWarnings("PersistenceUnitPresent")

public class JournalEntryLines implements Serializable {

    @Id
    @SequenceGenerator(name = "journal_entry_lines_seq", sequenceName = "journal_entry_lines_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_entry_lines_seq")
    @Column(name = "journal_entry_line_id", columnDefinition = "INTEGER")
    private Integer journalEntryLineId;

    @Column(name = "journal_entry", insertable = false, updatable = false)
    private Integer journalEntry;

    @Column(name = "line_no")
    private Integer lineNo;

    @Column(name = "account_slot")
    private Integer accountSlot;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "debit_amount")
    private Double debitAmount;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "credit_amount")
    private Double creditAmount;

    @Column(name = "description")
    private String description;

    public Integer getJournalEntryLineId() {
        return journalEntryLineId;
    }

    public void setJournalEntryLineId(Integer journalEntryLineId) {
        this.journalEntryLineId = journalEntryLineId;
    }

    public Integer getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(Integer journalEntry) {
        this.journalEntry = journalEntry;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }
    public Double getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Double debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JournalEntryLines that)) {
            return false;
        }

        return journalEntryLineId != null
                && journalEntryLineId.equals(that.journalEntryLineId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
