package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "yield_curve")
@SuppressWarnings("PersistenceUnitPresent")

public class YieldCurve implements Serializable {

    @Id
    @SequenceGenerator(name = "yield_curve_seq", sequenceName = "yield_curve_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "yield_curve_seq")
    @Column(name = "id_yield_curve", columnDefinition = "INTEGER")
    private Integer idYieldCurve;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar", nullable = true)
    private Calendar calendar;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "yield_curve", nullable = false) // FK in child table yield_curve_item
    private List<YieldCurveItem> items = new ArrayList<>();

    @Column(name = "compounding")
    private Short compounding;
    
    @Column(name = "provider")
    private String provider;    

    public Integer getIdYieldCurve() {
        return idYieldCurve;
    }

    public void setIdYieldCurve(Integer idYieldCurve) {
        this.idYieldCurve = idYieldCurve;
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
        if (getIdYieldCurve() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        YieldCurve that = (YieldCurve) obj;
        return getIdYieldCurve().equals(that.getIdYieldCurve());
    }

    @Override
    public int hashCode() {
        return getIdYieldCurve() == null ? 0 : idYieldCurve.hashCode();
    }

    @Override
    public String toString() {
        return code;
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
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the items
     */
    public List<YieldCurveItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<YieldCurveItem> items) {
        this.items = items;
    }

    /**
     * @return the compounding
     */
    public Short getCompounding() {
        return compounding;
    }

    /**
     * @param compounding the compounding to set
     */
    public void setCompounding(Short compounding) {
        this.compounding = compounding;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

}
