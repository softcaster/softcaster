package org.softcaster.easy_pricer_core.data;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar", nullable = true)
    private Calendar calendar;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "yield_curve") // FK in child table yield_curve_item
    private List<YieldCurveItem> items = new ArrayList<>();    
    
    @Column(name = "compounding")
    private Short compounding;
    
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
    
}
