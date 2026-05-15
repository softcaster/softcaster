package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "yield_curve_item")
@SuppressWarnings("PersistenceUnitPresent")

public class YieldCurveItem implements Serializable {

    @Id
    @SequenceGenerator(name = "yield_curve_item_seq", sequenceName = "yield_curve_item_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "yield_curve_item_seq")
    @Column(name = "id_yield_curve_item", columnDefinition = "INTEGER")
    private Integer idYieldCurveItem;

    @Column(name = "yield_curve", insertable = false, updatable = false)
    private Integer yieldCurve;

    @Column(name = "ric")
    private String ric;

    @Column(name = "offset_type")
    private Short offsetType;

    @Column(name = "offset_value")
    private Short offsetValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "bid")
    private Double bid;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ask")
    private Double ask;

    @Column(name = "compounding")
    private Short compounding;

    @Column(name = "daycount")
    private Short daycount;

    public Integer getIdYieldCurveItem() {
        return idYieldCurveItem;
    }

    public void setIdYieldCurveItem(Integer idYieldCurveItem) {
        this.idYieldCurveItem = idYieldCurveItem;
    }

    public Integer getYieldCurve() {
        return yieldCurve;
    }

    public void setYieldCurve(Integer yieldCurve) {
        this.yieldCurve = yieldCurve;
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public Short getOffsetType() {
        return offsetType;
    }

    public void setOffsetType(Short offsetType) {
        this.offsetType = offsetType;
    }

    public Short getOffsetValue() {
        return offsetValue;
    }

    public void setOffsetValue(Short offsetValue) {
        this.offsetValue = offsetValue;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdYieldCurveItem() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        YieldCurveItem that = (YieldCurveItem) obj;
        return getIdYieldCurveItem().equals(that.getIdYieldCurveItem());
    }

    @Override
    public int hashCode() {
        return getIdYieldCurveItem() == null ? 0 : idYieldCurveItem.hashCode();
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
     * @return the daycount
     */
    public Short getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(Short daycount) {
        this.daycount = daycount;
    }
}
