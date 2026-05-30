package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounting_status")
@SuppressWarnings("PersistenceUnitPresent")

public class AccountingStatus implements Serializable {

    @Id
    @SequenceGenerator(name = "accounting_status_seq", sequenceName = "accounting_status_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accounting_status_seq")
    @Column(name = "accounting_status_id", columnDefinition = "INTEGER")
    private Integer accountingStatusId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getAccountingStatusId() {
        return accountingStatusId;
    }

    public void setAccountingStatusId(Integer accountingStatusId) {
        this.accountingStatusId = accountingStatusId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        if (getAccountingStatusId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccountingStatus that = (AccountingStatus) obj;
        return getAccountingStatusId().equals(that.getAccountingStatusId());
    }

    @Override
    public int hashCode() {
        return getAccountingStatusId() == null ? 0 : accountingStatusId.hashCode();
    }
}
