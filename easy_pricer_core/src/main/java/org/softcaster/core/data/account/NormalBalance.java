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
@Table(name = "normal_balances")
@SuppressWarnings("PersistenceUnitPresent")

public class NormalBalance implements Serializable {

    @Id
    @SequenceGenerator(name = "normal_balances_seq", sequenceName = "normal_balances_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "normal_balances_seq")
    @Column(name = "balance_id", columnDefinition = "INTEGER")
    private Integer balanceId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
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
        if (getBalanceId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NormalBalance that = (NormalBalance) obj;
        return getBalanceId().equals(that.getBalanceId());
    }

    @Override
    public int hashCode() {
        return getBalanceId() == null ? 0 : balanceId.hashCode();
    }
}
