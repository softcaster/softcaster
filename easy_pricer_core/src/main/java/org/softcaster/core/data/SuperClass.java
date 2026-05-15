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
@Table(name = "super_class")
@SuppressWarnings("PersistenceUnitPresent")

public class SuperClass implements Serializable {

    @Id
    @SequenceGenerator(name = "super_class_seq", sequenceName = "super_class_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "super_class_seq")
    @Column(name = "id_super_class", columnDefinition = "INTEGER")
    private Integer idSuperClass;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdSuperClass() {
        return idSuperClass;
    }

    public void setIdSuperClass(Integer idSuperClass) {
        this.idSuperClass = idSuperClass;
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
        if (getIdSuperClass() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SuperClass that = (SuperClass) obj;
        return getIdSuperClass().equals(that.getIdSuperClass());
    }

    @Override
    public int hashCode() {
        return getIdSuperClass() == null ? 0 : idSuperClass.hashCode();
    }
}
