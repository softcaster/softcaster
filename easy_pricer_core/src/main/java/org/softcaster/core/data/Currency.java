package org.softcaster.core.data;

import java.io.Serializable;
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
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.engine.enums.DaycountBasis;

@Entity
@Table(name = "currency")
@SuppressWarnings("PersistenceUnitPresent")

public class Currency implements Serializable {

    @Id
    @SequenceGenerator(name = "currency_seq", sequenceName = "currency_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "currency_seq")
    @Column(name = "id_currency", columnDefinition = "INTEGER")
    private Integer idCurrency;

    @Column(name = "iso_code")
    private String isoCode;

    @Column(name = "currency_numeric_code")
    private Short currencyNumericCode;

    @Column(name = "description")
    private String description;

    @Column(name = "minor_unit")
    private Short minorUnit;

    @Column(name = "system_curr")
    private Short systemCurr;

    @Column(name = "physical_curr")
    private Short physicalCurr;

    @Column(name = "business_days")
    private Integer businessDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar", nullable = true)
    private Calendar calendar;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount")
    private DaycountBasis daycount;

    @Column(name = "decimal_places")
    private Short decimalPlaces;

    public Integer getIdCurrency() {
        return idCurrency;
    }

    public void setIdCurrency(Integer idCurrency) {
        this.idCurrency = idCurrency;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Short getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(Short currencyNumericCode) {
        this.currencyNumericCode = currencyNumericCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getMinorUnit() {
        return minorUnit;
    }

    public void setMinorUnit(Short minorUnit) {
        this.minorUnit = minorUnit;
    }

    public Short getSystemCurr() {
        return systemCurr;
    }

    public void setSystemCurr(Short systemCurr) {
        this.systemCurr = systemCurr;
    }

    public Short getPhysicalCurr() {
        return physicalCurr;
    }

    public void setPhysicalCurr(Short physicalCurr) {
        this.physicalCurr = physicalCurr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdCurrency() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Currency that = (Currency) obj;
        return getIdCurrency().equals(that.getIdCurrency());
    }

    @Override
    public int hashCode() {
        return getIdCurrency() == null ? 0 : idCurrency.hashCode();
    }

    @Override
    public String toString() {
        return isoCode;
    }

    /**
     * @return the businessDays
     */
    public Integer getBusinessDays() {
        return businessDays;
    }

    /**
     * @param businessDays the businessDays to set
     */
    public void setBusinessDays(Integer businessDays) {
        this.businessDays = businessDays;
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
     * @return the decimalPlaces
     */
    public Short getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * @param decimalPlaces the decimalPlaces to set
     */
    public void setDecimalPlaces(Short decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

}
