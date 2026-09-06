package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "ref_rate_fixing")
@SuppressWarnings("PersistenceUnitPresent")

public class RefRateFixing implements Serializable {

    @Id
    @SequenceGenerator(name = "ref_rate_fixing_seq", sequenceName = "ref_rate_fixing_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ref_rate_fixing_seq")
    @Column(name = "ref_rate_fixing_id")
    private Integer refRateFixingId;

    @Column(name = "ref_rate_index", insertable = false, updatable = false)
    private Integer refRateIndex;

    @Column(name = "fixing_date")
    private java.sql.Date fixingDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "rate")
    private Double rate;

    public Integer getRefRateFixingId() {
        return refRateFixingId;
    }

    public void setRefRateFixingId(Integer refRateFixingId) {
        this.refRateFixingId = refRateFixingId;
    }

    public Integer getRefRateIndex() {
        return refRateIndex;
    }

    public void setRefRateIndex(Integer refRateIndex) {
        this.refRateIndex = refRateIndex;
    }

    public java.sql.Date getFixingDate() {
        return fixingDate;
    }

    public void setFixingDate(java.sql.Date fixingDate) {
        this.fixingDate = fixingDate;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RefRateFixing that)) {
            return false;
        }

        return refRateFixingId != null
                && refRateFixingId.equals(that.refRateFixingId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
