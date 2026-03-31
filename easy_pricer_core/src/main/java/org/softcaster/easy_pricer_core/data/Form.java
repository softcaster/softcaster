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
@Table(name = "form")
@SuppressWarnings("PersistenceUnitPresent")

public class Form implements Serializable {

    @Id
    @SequenceGenerator(name = "form_seq", sequenceName = "form_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "form_seq")
    @Column(name = "id_form", columnDefinition = "INTEGER")
    private Integer idForm;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdForm() {
        return idForm;
    }

    public void setIdForm(Integer idForm) {
        this.idForm = idForm;
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
        if (getIdForm() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Form that = (Form) obj;
        return getIdForm().equals(that.getIdForm());
    }

    @Override
    public int hashCode() {
        return getIdForm() == null ? 0 : idForm.hashCode();
    }
}
