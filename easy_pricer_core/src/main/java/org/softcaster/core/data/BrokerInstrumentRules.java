package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "broker_instrument_rules")
@SuppressWarnings("PersistenceUnitPresent")

public class BrokerInstrumentRules implements Serializable {

    @Id
    @SequenceGenerator(name = "broker_instrument_rules_seq", sequenceName = "broker_instrument_rules_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "broker_instrument_rules_seq")
    @Column(name = "broker_rule_id")
    private Integer brokerRuleId;

    @Column(name = "broker")
    private Integer broker;

    @Column(name = "master_data")
    private Integer masterData;

    @Column(name = "txn_side")
    private Short txnSide;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "initial_margin")
    private Double initialMargin;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "maintenance_margin")
    private Double maintenanceMargin;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "broker_fee")
    private Double brokerFee;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "exchange_fee")
    private Double exchangeFee;

    @Column(name = "currency")
    private Integer currency;

    public Integer getBrokerRuleId() {
        return brokerRuleId;
    }

    public void setBrokerRuleId(Integer brokerRuleId) {
        this.brokerRuleId = brokerRuleId;
    }

    public Integer getBroker() {
        return broker;
    }

    public void setBroker(Integer broker) {
        this.broker = broker;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    public Short getTxnSide() {
        return txnSide;
    }

    public void setTxnSide(Short txnSide) {
        this.txnSide = txnSide;
    }

    public Double getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(Double initialMargin) {
        this.initialMargin = initialMargin;
    }

    public Double getMaintenanceMargin() {
        return maintenanceMargin;
    }

    public void setMaintenanceMargin(Double maintenanceMargin) {
        this.maintenanceMargin = maintenanceMargin;
    }

    public Double getBrokerFee() {
        return brokerFee;
    }

    public void setBrokerFee(Double brokerFee) {
        this.brokerFee = brokerFee;
    }

    public Double getExchangeFee() {
        return exchangeFee;
    }

    public void setExchangeFee(Double exchangeFee) {
        this.exchangeFee = exchangeFee;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getBrokerRuleId() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BrokerInstrumentRules that = (BrokerInstrumentRules) obj;
        return getBrokerRuleId().equals(that.getBrokerRuleId());
    }

    @Override
    public int hashCode() {
        return getBrokerRuleId() == null ? 0 : brokerRuleId.hashCode();
    }
}
