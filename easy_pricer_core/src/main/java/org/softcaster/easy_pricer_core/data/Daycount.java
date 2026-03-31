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
@Table(name = "daycount")
@SuppressWarnings("PersistenceUnitPresent")

public class Daycount implements Serializable {

    @Id
    @SequenceGenerator(name = "daycount_seq", sequenceName = "daycount_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "daycount_seq")
    @Column(name = "id_daycount", columnDefinition = "INTEGER")
    private Integer idDaycount;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdDaycount() {
        return idDaycount;
    }

    public void setIdDaycount(Integer idDaycount) {
        this.idDaycount = idDaycount;
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
        if (getIdDaycount() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Daycount that = (Daycount) obj;
        return getIdDaycount().equals(that.getIdDaycount());
    }

    @Override
    public int hashCode() {
        return getIdDaycount() == null ? 0 : idDaycount.hashCode();
    }
}
