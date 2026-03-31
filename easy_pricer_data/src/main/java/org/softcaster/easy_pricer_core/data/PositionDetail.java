package org.softcaster.easy_pricer_core.data;

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

    @Column(name = "position_md")
    private Integer positionMd;

    @Column(name = "master_data")
    private Integer masterData;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "realized_pnl")
    private Double realizedPnl;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "unrealized_pnl")
    private Double unrealizedPnl;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "avg_price")
    private Double avgPrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "market_value")
    private Double marketValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "net_quantity")
    private Double netQuantity;

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

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public Double getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(Double netQuantity) {
        this.netQuantity = netQuantity;
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
}
