package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.core.data.converters.CounterpartyRoleConverter;
import org.softcaster.engine.enums.CounterpartyRole;

@Entity
@Table(name = "counterparty_role_mapping")
@SuppressWarnings("PersistenceUnitPresent")

public class CounterpartyRoleMapping implements Serializable {

    @Id
    @SequenceGenerator(name = "counterparty_role_mapping_seq", sequenceName = "counterparty_role_mapping_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "counterparty_role_mapping_seq")
    @Column(name = "counterparty_role_mapping_id")
    private Integer counterpartyRoleMappingId;

    @Column(name = "counterparty", insertable = false, updatable = false)
    private Integer counterparty;

    @Convert(converter = CounterpartyRoleConverter.class)
    @Column(name = "ctp_role") // Questa colonna sul DB rimane un INTEGER (int4)
    private CounterpartyRole ctpRole;

    public Integer getCounterpartyRoleMappingId() {
        return counterpartyRoleMappingId;
    }

    public void setCounterpartyRoleMappingId(Integer counterpartyRoleMappingId) {
        this.counterpartyRoleMappingId = counterpartyRoleMappingId;
    }

    public Integer getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Integer counterparty) {
        this.counterparty = counterparty;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getCounterpartyRoleMappingId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CounterpartyRoleMapping that = (CounterpartyRoleMapping) obj;
        return getCounterpartyRoleMappingId().equals(that.getCounterpartyRoleMappingId());
    }

    @Override
    public int hashCode() {
        return getCounterpartyRoleMappingId() == null ? 0 : counterpartyRoleMappingId.hashCode();
    }

    /**
     * @return the ctpRole
     */
    public CounterpartyRole getCtpRole() {
        return ctpRole;
    }

    /**
     * @param ctpRole the ctpRole to set
     */
    public void setCtpRole(CounterpartyRole ctpRole) {
        this.ctpRole = ctpRole;
    }
}
