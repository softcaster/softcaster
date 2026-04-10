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
@Table(name = "issuer")
@SuppressWarnings("PersistenceUnitPresent")

public class Issuer implements Serializable {

    @Id
    @SequenceGenerator(name = "issuer_seq", sequenceName = "issuer_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "issuer_seq")
    @Column(name = "id_issuer", columnDefinition = "INTEGER")
    private Integer idIssuer;

    @Column(name = "short_issuer_name")
    private String shortIssuerName;

    @Column(name = "long_issuer_name")
    private String longIssuerName;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country", nullable = true)
    private Country country;

    public Integer getIdIssuer() {
        return idIssuer;
    }

    public void setIdIssuer(Integer idIssuer) {
        this.idIssuer = idIssuer;
    }

    public String getLongIssuerName() {
        return longIssuerName;
    }

    public void setLongIssuerName(String longIssuerName) {
        this.longIssuerName = longIssuerName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the shortIssuerName
     */
    public String getShortIssuerName() {
        return shortIssuerName;
    }

    /**
     * @param shortIssuerName the shortIssuerName to set
     */
    public void setShortIssuerName(String shortIssuerName) {
        this.shortIssuerName = shortIssuerName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdIssuer() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Issuer that = (Issuer) obj;
        return getIdIssuer().equals(that.getIdIssuer());
    }

    @Override
    public int hashCode() {
        return getIdIssuer() == null ? 0 : idIssuer.hashCode();
    }
    
    @Override
    public String toString() {
        return shortIssuerName;
    }    
}
