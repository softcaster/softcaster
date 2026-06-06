package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.CounterpartyTypeConverter;
import org.softcaster.engine.enums.CounterpartyRole;
import org.softcaster.engine.enums.CounterpartyType;

@Entity
@Table(name = "counterparty")
@SuppressWarnings("PersistenceUnitPresent")

public class Counterparty implements Serializable {

    @Id
    @SequenceGenerator(name = "counterparty_seq", sequenceName = "counterparty_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "counterparty_seq")
    @Column(name = "id_counterparty", columnDefinition = "INTEGER")
    private Integer idCounterparty;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country", nullable = true)
    private Country country;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "lei_code")
    private String leiCode;

    @Convert(converter = CounterpartyTypeConverter.class)
    @Column(name = "ctp_type")
    private CounterpartyType ctpType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "counterparty", nullable = false) // FK in child table holiday
    private List<CounterpartyRoleMapping> roles = new ArrayList<>();

    public Integer getIdCounterparty() {
        return idCounterparty;
    }

    public void setIdCounterparty(Integer idCounterparty) {
        this.idCounterparty = idCounterparty;
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
        if (getIdCounterparty() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Counterparty that = (Counterparty) obj;
        return getIdCounterparty().equals(that.getIdCounterparty());
    }

    @Override
    public int hashCode() {
        return getIdCounterparty() == null ? 0 : idCounterparty.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * @return the ctpType
     */
    public CounterpartyType getCtpType() {
        return ctpType;
    }

    /**
     * @param ctpType the ctpType to set
     */
    public void setCtpType(CounterpartyType ctpType) {
        this.ctpType = ctpType;
    }

    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the leiCode
     */
    public String getLeiCode() {
        return leiCode;
    }

    /**
     * @param leiCode the leiCode to set
     */
    public void setLeiCode(String leiCode) {
        this.leiCode = leiCode;
    }

    /**
     * @return the roles
     */
    public List<CounterpartyRoleMapping> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<CounterpartyRoleMapping> roles) {
        this.roles = roles;
    }

    public boolean hasRole(CounterpartyRole targetRole) {
        if (this.roles == null || targetRole == null) {
            return false;
        }

        // Scorre la lista in modo efficiente e si ferma al primo match trovato
        return this.roles.stream()
                .anyMatch(mapping -> mapping.getCtpRole() == targetRole);
    }
}
