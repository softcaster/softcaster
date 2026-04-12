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
@Table(name = "counterparty_type")
@SuppressWarnings("PersistenceUnitPresent")

public class CounterpartyType implements Serializable {

    @Id
    @SequenceGenerator(name = "counterparty_type_seq", sequenceName = "counterparty_type_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "counterparty_type_seq")
    @Column(name = "id_counterparty_type", columnDefinition = "INTEGER")
    private Integer idCounterpartyType;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdCounterpartyType() {
        return idCounterpartyType;
    }

    public void setIdCounterpartyType(Integer idCounterpartyType) {
        this.idCounterpartyType = idCounterpartyType;
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
        if (getIdCounterpartyType() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CounterpartyType that = (CounterpartyType) obj;
        return getIdCounterpartyType().equals(that.getIdCounterpartyType());
    }

    @Override
    public int hashCode() {
        return getIdCounterpartyType() == null ? 0 : idCounterpartyType.hashCode();
    }
    
     @Override
    public String toString() {
        return code;
    }
}
