package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "financial_statement_types")
@SuppressWarnings("PersistenceUnitPresent")

public class FinancialStatementType implements Serializable {

    @Id
    @SequenceGenerator(name = "financial_statement_types_seq", sequenceName = "financial_statement_types_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_statement_types_seq")
    @Column(name = "statement_type_id", columnDefinition = "INTEGER")
    private Integer statementTypeId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getStatementTypeId() {
        return statementTypeId;
    }

    public void setStatementTypeId(Integer statementTypeId) {
        this.statementTypeId = statementTypeId;
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
        if (getStatementTypeId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FinancialStatementType that = (FinancialStatementType) obj;
        return getStatementTypeId().equals(that.getStatementTypeId());
    }

    @Override
    public int hashCode() {
        return getStatementTypeId() == null ? 0 : statementTypeId.hashCode();
    }
}
