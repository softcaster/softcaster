package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "flt_security_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class FltSecurityMasterData extends SecurityMasterData {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "spread")
    private Double spread;

    @Column(name = "index")
    private String index;

    @Column(name = "coupon_pm")
    private Integer couponPm;

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

    public Integer getCouponPm() {
        return couponPm;
    }

    public void setCouponPm(Integer couponPm) {
        this.couponPm = couponPm;
    }

}
