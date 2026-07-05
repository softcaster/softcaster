package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.engine.enums.DaycountBasis;

@Entity
@Table(name = "accounting_event_accruals")
@SuppressWarnings("PersistenceUnitPresent")

public class AccountingEventAccruals extends AccountingEvent {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "accounting_nominal")
    private Double accountingNominal;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "accrual_amount")
    private Double accrualAmount;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "coupon_rate")
    private Double couponRate;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount")
    private DaycountBasis daycount;

    @Column(name = "days")
    private Integer days;

    public Double getAccountingNominal() {
        return accountingNominal;
    }

    public void setAccountingNominal(Double accountingNominal) {
        this.accountingNominal = accountingNominal;
    }

    public Double getAccrualAmount() {
        return accrualAmount;
    }

    public void setAccrualAmount(Double accrualAmount) {
        this.accrualAmount = accrualAmount;
    }

    public Double getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(Double couponRate) {
        this.couponRate = couponRate;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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
