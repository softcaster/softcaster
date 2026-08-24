package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.CashFlowStatusConverter;
import org.softcaster.engine.enums.CashFlowStatus;

@Entity
@Table(name = "cash_flow_item")
@SuppressWarnings("PersistenceUnitPresent")

public class CashFlowItem implements Serializable {

    @Id
    @SequenceGenerator(name = "cash_flow_item_seq", sequenceName = "cash_flow_item_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cash_flow_item_seq")
    @Column(name = "id_cash_flow_item", columnDefinition = "INTEGER")
    private Integer idCashFlowItem;

    @Column(name = "master_data", insertable = false, updatable = false)
    private Integer masterData;

    @Column(name = "start_date")
    private java.sql.Date startDate;

    @Column(name = "end_date")
    private java.sql.Date endDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "interest")
    private Double interest;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "amount")
    private Double amount;

    @Convert(converter = CashFlowStatusConverter.class)
    @Column(name = "cash_flow_status")
    private CashFlowStatus cashFlowStatus;

    public Integer getIdCashFlowItem() {
        return idCashFlowItem;
    }

    public void setIdCashFlowItem(Integer idCashFlowItem) {
        this.idCashFlowItem = idCashFlowItem;
    }

    public java.sql.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.sql.Date startDate) {
        this.startDate = startDate;
    }

    public java.sql.Date getEnddate() {
        return endDate;
    }

    public void setEndDate(java.sql.Date endDate) {
        this.endDate = endDate;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdCashFlowItem() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CashFlowItem that = (CashFlowItem) obj;
        return getIdCashFlowItem().equals(that.getIdCashFlowItem());
    }

    @Override
    public int hashCode() {
        return getIdCashFlowItem() == null ? 0 : idCashFlowItem.hashCode();
    }

    /**
     * @return the masterData
     */
    public Integer getMasterData() {
        return masterData;
    }

    /**
     * @param masterData the masterData to set
     */
    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    /**
     * @return the cashFlowStatus
     */
    public CashFlowStatus getCashFlowStatus() {
        return cashFlowStatus;
    }

    /**
     * @param cashFlowStatus the cashFlowStatus to set
     */
    public void setCashFlowStatus(CashFlowStatus cashFlowStatus) {
        this.cashFlowStatus = cashFlowStatus;
    }

}
