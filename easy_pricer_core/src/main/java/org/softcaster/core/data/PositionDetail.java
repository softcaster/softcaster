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
@Table(name = "position_detail")
@SuppressWarnings("PersistenceUnitPresent")

public class PositionDetail implements Serializable {

    @Id
    @SequenceGenerator(name = "position_detail_seq", sequenceName = "position_detail_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "position_detail_seq")
    @Column(name = "id_position_detail", columnDefinition = "INTEGER")
    private Integer idPositionDetail;

    @Column(name = "position_md", nullable = false)
    private Integer positionMd;

    @Column(name = "master_data", nullable = false)
    private Integer masterData;

    @Column(name = "counterparty", nullable = false)
    private Integer counterparty;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "realized_pnl")
    private Double realizedPnl;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "unrealized_pnl")
    private Double unrealizedPnl;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "buy_qty")
    private Double buyQty;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "notional_value_buy")
    private Double notionalValueBuy;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "buy_fees")
    private Double buyFees;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "buy_taxes")
    private Double buyTaxes;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "sell_qty")
    private Double sellQty;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "notional_value_sell")
    private Double notionalValueSell;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "sell_fees")
    private Double sellFees;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "sell_taxes")
    private Double sellTaxes;

   @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "market_price")
    private Double marketPrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "buy_accrual")
    private Double buyAccrual;
    
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "sell_accrual")
    private Double sellAccrual;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ytm")
    private Double ytm;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "duration")
    private Double duration;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "mod_duration")
    private Double modDuration;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "time_to_maturity")
    private Double timeToMaturity;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "theoretical_price")
    private Double theoreticalPrice;
    
    public Integer getIdPositionDetail() {
        return idPositionDetail;
    }

    public void setIdPositionDetail(Integer idPositionDetail) {
        this.idPositionDetail = idPositionDetail;
    }

    public Integer getPositionMd() {
        return positionMd;
    }

    public void setPositionMd(Integer positionMd) {
        this.positionMd = positionMd;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    public Double getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(Double realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public Double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(Double unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdPositionDetail() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PositionDetail that = (PositionDetail) obj;
        return getIdPositionDetail().equals(that.getIdPositionDetail());
    }

    @Override
    public int hashCode() {
        return getIdPositionDetail() == null ? 0 : idPositionDetail.hashCode();
    }

    /**
     * @return the buyQty
     */
    public Double getBuyQty() {
        return buyQty;
    }

    /**
     * @param buyQty the buyQty to set
     */
    public void setBuyQty(Double buyQty) {
        this.buyQty = buyQty;
    }

    /**
     * @return the sellQty
     */
    public Double getSellQty() {
        return sellQty;
    }

    /**
     * @param sellQty the sellQty to set
     */
    public void setSellQty(Double sellQty) {
        this.sellQty = sellQty;
    }

    /**
     * @return the marketPrice
     */
    public Double getMarketPrice() {
        return marketPrice;
    }

    /**
     * @param marketPrice the marketPrice to set
     */
    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    /**
     * @return the notionalValueBuy
     */
    public Double getNotionalValueBuy() {
        return notionalValueBuy;
    }

    /**
     * @param notionalValueBuy the notionalValueBuy to set
     */
    public void setNotionalValueBuy(Double notionalValueBuy) {
        this.notionalValueBuy = notionalValueBuy;
    }

    /**
     * @return the buyFees
     */
    public Double getBuyFees() {
        return buyFees;
    }

    /**
     * @param buyFees the buyFees to set
     */
    public void setBuyFees(Double buyFees) {
        this.buyFees = buyFees;
    }

    /**
     * @return the buyTaxes
     */
    public Double getBuyTaxes() {
        return buyTaxes;
    }

    /**
     * @param buyTaxes the buyTaxes to set
     */
    public void setBuyTaxes(Double buyTaxes) {
        this.buyTaxes = buyTaxes;
    }

    /**
     * @return the notionalValueSell
     */
    public Double getNotionalValueSell() {
        return notionalValueSell;
    }

    /**
     * @param notionalValueSell the notionalValueSell to set
     */
    public void setNotionalValueSell(Double notionalValueSell) {
        this.notionalValueSell = notionalValueSell;
    }

    /**
     * @return the sellFees
     */
    public Double getSellFees() {
        return sellFees;
    }

    /**
     * @param sellFees the sellFees to set
     */
    public void setSellFees(Double sellFees) {
        this.sellFees = sellFees;
    }

    /**
     * @return the sellTaxes
     */
    public Double getSellTaxes() {
        return sellTaxes;
    }

    /**
     * @param sellTaxes the sellTaxes to set
     */
    public void setSellTaxes(Double sellTaxes) {
        this.sellTaxes = sellTaxes;
    }

    /**
     * @return the counterparty
     */
    public Integer getCounterparty() {
        return counterparty;
    }

    /**
     * @param counterparty the counterparty to set
     */
    public void setCounterparty(Integer counterparty) {
        this.counterparty = counterparty;
    }

    public void initialize() {
        realizedPnl = unrealizedPnl = buyQty = notionalValueBuy = buyFees = buyTaxes
                = sellQty = notionalValueSell = sellFees = sellTaxes = marketPrice 
                = buyAccrual = sellAccrual = ytm = duration = modDuration = timeToMaturity 
                = theoreticalPrice = 0.;
    }

    /**
     * @return the ytm
     */
    public Double getYtm() {
        return ytm;
    }

    /**
     * @param ytm the ytm to set
     */
    public void setYtm(Double ytm) {
        this.ytm = ytm;
    }

    /**
     * @return the duration
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    /**
     * @return the modDuration
     */
    public Double getModDuration() {
        return modDuration;
    }

    /**
     * @param modDuration the modDuration to set
     */
    public void setModDuration(Double modDuration) {
        this.modDuration = modDuration;
    }

    /**
     * @return the timeToMaturity
     */
    public Double getTimeToMaturity() {
        return timeToMaturity;
    }

    /**
     * @param timeToMaturity the timeToMaturity to set
     */
    public void setTimeToMaturity(Double timeToMaturity) {
        this.timeToMaturity = timeToMaturity;
    }

    /**
     * @return the theoreticalPrice
     */
    public Double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    /**
     * @param theoreticalPrice the theoreticalPrice to set
     */
    public void setTheoreticalPrice(Double theoreticalPrice) {
        this.theoreticalPrice = theoreticalPrice;
    }

    /**
     * @return the buyAccrual
     */
    public Double getBuyAccrual() {
        return buyAccrual;
    }

    /**
     * @param buyAccrual the buyAccrual to set
     */
    public void setBuyAccrual(Double buyAccrual) {
        this.buyAccrual = buyAccrual;
    }

    /**
     * @return the sellAccrual
     */
    public Double getSellAccrual() {
        return sellAccrual;
    }

    /**
     * @param sellAccrual the sellAccrual to set
     */
    public void setSellAccrual(Double sellAccrual) {
        this.sellAccrual = sellAccrual;
    }
}
