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
@Table(name = "amortization_schedule")
@SuppressWarnings("PersistenceUnitPresent")

public class AmortizationSchedule implements Serializable {

    @Id
    @SequenceGenerator(name = "amortization_schedule_seq", sequenceName = "amortization_schedule_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "amortization_schedule_seq")
    @Column(name = "id_amortization_schedule", columnDefinition = "INTEGER")
    private Integer idAmortizationSchedule;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdAmortizationSchedule() {
        return idAmortizationSchedule;
    }

    public void setIdAmortizationSchedule(Integer idAmortizationSchedule) {
        this.idAmortizationSchedule = idAmortizationSchedule;
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
        if (getIdAmortizationSchedule() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AmortizationSchedule that = (AmortizationSchedule) obj;
        return getIdAmortizationSchedule().equals(that.getIdAmortizationSchedule());
    }

    @Override
    public int hashCode() {
        return getIdAmortizationSchedule() == null ? 0 : idAmortizationSchedule.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }    
}
