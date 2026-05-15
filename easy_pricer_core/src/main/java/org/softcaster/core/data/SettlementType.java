package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlement_type")
@SuppressWarnings("PersistenceUnitPresent")

public class SettlementType implements Serializable {

    @Id
    @SequenceGenerator(name = "settlement_type_seq", sequenceName = "settlement_type_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "settlement_type_seq")
    @Column(name = "id_settlement_type", columnDefinition = "INTEGER")
    private Integer idSettlementType;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdSettlementType() {
        return idSettlementType;
    }

    public void setIdSettlementType(Integer idSettlementType) {
        this.idSettlementType = idSettlementType;
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
        if (getIdSettlementType() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SettlementType that = (SettlementType) obj;
        return getIdSettlementType().equals(that.getIdSettlementType());
    }

    @Override
    public int hashCode() {
        return getIdSettlementType() == null ? 0 : idSettlementType.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
