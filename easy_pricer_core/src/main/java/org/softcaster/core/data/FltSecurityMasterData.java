package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.CouponProjectionMethodConverter;
import org.softcaster.engine.enums.CouponProjectionMethod;

@Entity
@Table(name = "flt_security_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class FltSecurityMasterData extends SecurityMasterData {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "spread")
    private Double spread;

    @Column(name = "index")
    private String index;

    @Convert(converter = CouponProjectionMethodConverter.class)
    @Column(name = "coupon_pm")
    private CouponProjectionMethod couponPm;

    public Double getSpread() {
        return spread;
    }

    public void setSpread(Double spread) {
        this.spread = spread;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return the couponPm
     */
    public CouponProjectionMethod getCouponPm() {
        return couponPm;
    }

    /**
     * @param couponPm the couponPm to set
     */
    public void setCouponPm(CouponProjectionMethod couponPm) {
        this.couponPm = couponPm;
    }
}
