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
@Table(name = "asset_class")
@SuppressWarnings("PersistenceUnitPresent")

public class AssetClass implements Serializable {

    @Id
    @SequenceGenerator(name = "asset_class_seq", sequenceName = "asset_class_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "asset_class_seq")
    @Column(name = "id_asset_class", columnDefinition = "INTEGER")
    private Integer idAssetClass;

    @Column(name = "super_class")
    private Integer superClass;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdAssetClass() {
        return idAssetClass;
    }

    public void setIdAssetClass(Integer idAssetClass) {
        this.idAssetClass = idAssetClass;
    }

    public Integer getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Integer superClass) {
        this.superClass = superClass;
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
        if (getIdAssetClass() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AssetClass that = (AssetClass) obj;
        return getIdAssetClass().equals(that.getIdAssetClass());
    }

    @Override
    public int hashCode() {
        return getIdAssetClass() == null ? 0 : idAssetClass.hashCode();
    }
}
