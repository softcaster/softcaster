package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "account_natures")
@SuppressWarnings("PersistenceUnitPresent")

public class AccountNature implements Serializable {

    @Id
    @SequenceGenerator(name = "account_natures_seq", sequenceName = "account_natures_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_natures_seq")
    @Column(name = "nature_id", columnDefinition = "INTEGER")
    private Integer natureId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getNatureId() {
        return natureId;
    }

    public void setNatureId(Integer natureId) {
        this.natureId = natureId;
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
        if (getNatureId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccountNature that = (AccountNature) obj;
        return getNatureId().equals(that.getNatureId());
    }

    @Override
    public int hashCode() {
        return getNatureId() == null ? 0 : natureId.hashCode();
    }
}
