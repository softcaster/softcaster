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
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "ledger_balances")
@SuppressWarnings("PersistenceUnitPresent")

public class LedgerBalances implements Serializable {

    @Id
    @SequenceGenerator(name = "ledger_balances_seq", sequenceName = "ledger_balances_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ledger_balances_seq")
    @Column(name = "ledger_balance_id")
    private Integer ledgerBalanceId;

    @Column(name = "gl_account")
    private Integer glAccount;

    @Column(name = "currency")
    private Integer currency;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "opening_balance")
    private Double openingBalance;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "debit_turnover")
    private Double debitTurnover;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "credit_turnover")
    private Double creditTurnover;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "closing_balance")
    private Double closingBalance;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    public Integer getLedgerBalanceId() {
        return ledgerBalanceId;
    }

    public void setLedgerBalanceId(Integer ledgerBalanceId) {
        this.ledgerBalanceId = ledgerBalanceId;
    }

    public Integer getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(Integer glAccount) {
        this.glAccount = glAccount;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getDebitTurnover() {
        return debitTurnover;
    }

    public void setDebitTurnover(Double debitTurnover) {
        this.debitTurnover = debitTurnover;
    }

    public Double getCreditTurnover() {
        return creditTurnover;
    }

    public void setCreditTurnover(Double creditTurnover) {
        this.creditTurnover = creditTurnover;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LedgerBalances that)) {
            return false;
        }

        return ledgerBalanceId != null
                && ledgerBalanceId.equals(that.ledgerBalanceId);
    }

    @Override
    public int hashCode() {
        return ledgerBalanceId == null ? 0 : ledgerBalanceId.hashCode();
    }

    /**
     * @return the updateAt
     */
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    /**
     * @param updateAt the updateAt to set
     */
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

}
