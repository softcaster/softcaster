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
@Table(name = "roll_convention")
@SuppressWarnings("PersistenceUnitPresent")

public class RollConvention implements Serializable {

    @Id
    @SequenceGenerator(name = "roll_convention_seq", sequenceName = "roll_convention_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "roll_convention_seq")
    @Column(name = "id_roll_convention", columnDefinition = "INTEGER")
    private Integer idRollConvention;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdRollConvention() {
        return idRollConvention;
    }

    public void setIdRollConvention(Integer idRollConvention) {
        this.idRollConvention = idRollConvention;
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
        if (getIdRollConvention() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RollConvention that = (RollConvention) obj;
        return getIdRollConvention().equals(that.getIdRollConvention());
    }

    @Override
    public int hashCode() {
        return getIdRollConvention() == null ? 0 : idRollConvention.hashCode();
    }
}
