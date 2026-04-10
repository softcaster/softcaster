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
@Table(name = "type_of_interest")
@SuppressWarnings("PersistenceUnitPresent")

public class TypeOfInterest implements Serializable {

    @Id
    @SequenceGenerator(name = "type_of_interest_seq", sequenceName = "type_of_interest_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "type_of_interest_seq")
    @Column(name = "id_type_of_interest", columnDefinition = "INTEGER")
    private Integer idTypeOfInterest;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdTypeOfInterest() {
        return idTypeOfInterest;
    }

    public void setIdTypeOfInterest(Integer idTypeOfInterest) {
        this.idTypeOfInterest = idTypeOfInterest;
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
        if (getIdTypeOfInterest() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TypeOfInterest that = (TypeOfInterest) obj;
        return getIdTypeOfInterest().equals(that.getIdTypeOfInterest());
    }

    @Override
    public int hashCode() {
        return getIdTypeOfInterest() == null ? 0 : idTypeOfInterest.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }    
}
