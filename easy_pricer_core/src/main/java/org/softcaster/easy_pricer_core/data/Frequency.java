package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "frequency")
@SuppressWarnings("PersistenceUnitPresent")

public class Frequency implements Serializable {

    @Id
    @SequenceGenerator(name = "frequency_seq", sequenceName = "frequency_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "frequency_seq")
    @Column(name = "id_frequency", columnDefinition = "INTEGER")
    private Integer idFrequency;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "year_fraction")
    private Short yearFraction;

    public Integer getIdFrequency() {
        return idFrequency;
    }

    public void setIdFrequency(Integer idFrequency) {
        this.idFrequency = idFrequency;
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
    
    /**
     * @return the yearFraction
     */
    public Short getYearFraction() {
        return yearFraction;
    }

    /**
     * @param yearFraction the yearFraction to set
     */
    public void setYearFraction(Short yearFraction) {
        this.yearFraction = yearFraction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdFrequency() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Frequency that = (Frequency) obj;
        return getIdFrequency().equals(that.getIdFrequency());
    }

    @Override
    public int hashCode() {
        return getIdFrequency() == null ? 0 : idFrequency.hashCode();
    }
}
