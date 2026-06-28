package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.core.data.Currency;

@Entity
@Table(name = "account_mapping")
@SuppressWarnings("PersistenceUnitPresent")

public class AccountMapping implements Serializable {

    @Id
    @SequenceGenerator(name = "account_mapping_seq", sequenceName = "account_mapping_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_mapping_seq")
    @Column(name = "account_mapping_id")
    private Integer accountMappingId;

    @Column(name = "mapping_key")
    private String mappingKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account", nullable = false)
    private GlAccount glAccount;

    public Integer getAccountMappingId() {
        return accountMappingId;
    }

    public void setAccountMappingId(Integer accountMappingId) {
        this.accountMappingId = accountMappingId;
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey) {
        this.mappingKey = mappingKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AccountMapping that)) {
            return false;
        }

        return accountMappingId != null
                && accountMappingId.equals(that.accountMappingId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the glAccount
     */
    public GlAccount getGlAccount() {
        return glAccount;
    }

    /**
     * @param glAccount the glAccount to set
     */
    public void setGlAccount(GlAccount glAccount) {
        this.glAccount = glAccount;
    }
}
