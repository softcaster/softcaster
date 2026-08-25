package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.engine.enums.DaycountBasis;

@Entity
@Table(name = "ref_rate_index")
@SuppressWarnings("PersistenceUnitPresent")

public class RefRateIndex implements Serializable {

    @Id
    @SequenceGenerator(name = "ref_rate_index_seq", sequenceName = "ref_rate_index_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ref_rate_index_seq")
    @Column(name = "ref_rate_index_id")
    private Integer refRateIndexId;

    @Column(name = "code", nullable = false, length = 25)
    private String code;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = false)
    private Currency currency;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount", nullable = false)
    private DaycountBasis daycount;

    public Integer getRefRateIndexId() {
        return refRateIndexId;
    }

    public void setRefRateIndexId(Integer refRateIndexId) {
        this.refRateIndexId = refRateIndexId;
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

        if (!(obj instanceof RefRateIndex that)) {
            return false;
        }

        return refRateIndexId != null
                && refRateIndexId.equals(that.refRateIndexId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the daycount
     */
    public DaycountBasis getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(DaycountBasis daycount) {
        this.daycount = daycount;
    }
}
