package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.core.data.converters.TenorConverter;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Tenor;

@Entity
@Table(name = "ref_rate_index")
@SuppressWarnings("PersistenceUnitPresent")

public class RefRateIndex implements Serializable {

    @Id
    @SequenceGenerator(name = "ref_rate_index_seq", sequenceName = "ref_rate_index_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ref_rate_index_seq")
    @Column(name = "ref_rate_index_id")
    private Integer refRateIndexId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount")
    private DaycountBasis daycount;

    @Convert(converter = TenorConverter.class)
    @Column(name = "tenor")
    private Tenor tenor;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "ref_rate_index", nullable = false) // FK in child table ref_rate_fixing
    private List<RefRateFixing> fixings = new ArrayList<>();
    
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

    /**
     * @return the tenor
     */
    public Tenor getTenor() {
        return tenor;
    }

    /**
     * @param tenor the tenor to set
     */
    public void setTenor(Tenor tenor) {
        this.tenor = tenor;
    }

    /**
     * @return the fixings
     */
    public List<RefRateFixing> getFixings() {
        return fixings;
    }

    /**
     * @param fixings the fixings to set
     */
    public void setFixings(List<RefRateFixing> fixings) {
        this.fixings = fixings;
    }
}
