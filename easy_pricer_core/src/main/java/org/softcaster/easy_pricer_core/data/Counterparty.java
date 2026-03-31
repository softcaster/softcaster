package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "counterparty")
@SuppressWarnings("PersistenceUnitPresent")

public class Counterparty implements Serializable {

    @Id
    @SequenceGenerator(name = "counterparty_seq", sequenceName = "counterparty_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "counterparty_seq")
    @Column(name = "id_counterparty", columnDefinition = "INTEGER")
    private Integer idCounterparty;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ctp_type", nullable = true)
    private CounterpartyType ctpType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country", nullable = true)
    private Country country;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

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
}
