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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.converters.AccountNatureConverter;
import org.softcaster.core.data.converters.FinancialStatementTypeConverter;
import org.softcaster.core.data.converters.NormalBalanceConverter;
import org.softcaster.engine.enums.AccountNature;
import org.softcaster.engine.enums.FinancialStatementType;
import org.softcaster.engine.enums.NormalBalance;

@Entity
@Table(name = "gl_accounts")
@SuppressWarnings("PersistenceUnitPresent")

public class GlAccount implements Serializable {

    @Id
    @SequenceGenerator(name = "gl_accounts_seq", sequenceName = "gl_accounts_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gl_accounts_seq")
    @Column(name = "account_id")
    private Integer accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private GlAccount parent;

    // SIGNIFICATO: 
    // non creare una colonna o una join table qui, perché la relazione 
    // è già gestita dal campo parent nell’altra entity
    @OneToMany(mappedBy = "parent")
    private final List<GlAccount> children = new ArrayList<>();

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "is_postable")
    private boolean postable;

   @Convert(converter = FinancialStatementTypeConverter.class)
    @Column(name = "statement_type")
    private FinancialStatementType statementType;

    @Convert(converter = AccountNatureConverter.class)
    @Column(name = "nature")
    private AccountNature nature;

    @Convert(converter = NormalBalanceConverter.class)
    @Column(name = "balance")
    private NormalBalance balance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "account", nullable = false) // FK in child table GlAccountSlots
    private List<GlAccountSlots> slots = new ArrayList<>();    

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public GlAccount getParent() {
        return parent;
    }

    public void setParent(GlAccount parent) {
        this.parent = parent;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof GlAccount that)) {
            return false;
        }

        return accountId != null
                && accountId.equals(that.accountId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the children
     */
    public List<GlAccount> getChildren() {
        return children;
    }

    /**
     * @return the postable
     */
    public boolean isPostable() {
        return postable;
    }

    /**
     * @param postable the postable to set
     */
    public void setPostable(boolean postable) {
        this.postable = postable;
    }

    /**
     * @return the statementType
     */
    public FinancialStatementType getStatementType() {
        return statementType;
    }

    /**
     * @param statementType the statementType to set
     */
    public void setStatementType(FinancialStatementType statementType) {
        this.statementType = statementType;
    }

    /**
     * @return the nature
     */
    public AccountNature getNature() {
        return nature;
    }

    /**
     * @param nature the nature to set
     */
    public void setNature(AccountNature nature) {
        this.nature = nature;
    }

    /**
     * @return the balance
     */
    public NormalBalance getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(NormalBalance balance) {
        this.balance = balance;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addChild(GlAccount child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }

    /**
     * @return the slots
     */
    public List<GlAccountSlots> getSlots() {
        return slots;
    }

    /**
     * @param slots the slots to set
     */
    public void setSlots(List<GlAccountSlots> slots) {
        this.slots = slots;
    }
}
