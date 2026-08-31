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
@Table(name = "gl_account_slots")
@SuppressWarnings("PersistenceUnitPresent")

public class GlAccountSlots implements Serializable {

    @Id
    @SequenceGenerator(name = "gl_account_slots_seq", sequenceName = "gl_account_slots_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gl_account_slots_seq")
    @Column(name = "account_slot_id")
    private Integer accountSlotId;

    @Column(name = "account", insertable = false, updatable = false)
    private Integer account;

    @Column(name = "currency")
    private Integer currency;

    public Integer getAccountSlotId() {
        return accountSlotId;
    }

    public void setAccountSlotId(Integer accountSlotId) {
        this.accountSlotId = accountSlotId;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getAccountSlotId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GlAccountSlots that = (GlAccountSlots) obj;
        return getAccountSlotId().equals(that.getAccountSlotId());
    }

    @Override
    public int hashCode() {
        return getAccountSlotId() == null ? 0 : accountSlotId.hashCode();
    }
}
