/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.softcaster.core.data.converters.TxnComponentTypeConverter;
import org.softcaster.engine.enums.TxnComponentType;

/**
 *
 * @author ep
 */
@Entity
@Table(name = "financial_txn_components")
public class FinancialTxnComponent {

    @Id
    @SequenceGenerator(name = "financial_txn_components_seq", sequenceName = "financial_txn_components_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_txn_components_seq")
    @Column(name = "txn_component_id")
    private Integer txnComponentId;

    @Column(name = "financial_txn", insertable = false, updatable = false)
    private Integer financialTxn;

    @Convert(converter = TxnComponentTypeConverter.class)
    @Column(name = "component_type")
    private TxnComponentType componentType;

    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * @return the txnComponentId
     */
    public Integer getTxnComponentId() {
        return txnComponentId;
    }

    /**
     * @param txnComponentId the txnComponentId to set
     */
    public void setTxnComponentId(Integer txnComponentId) {
        this.txnComponentId = txnComponentId;
    }

    /**
     * @return the componentType
     */
    public TxnComponentType getComponentType() {
        return componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    public void setComponentType(TxnComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the financialTxn
     */
    public Integer getFinancialTxn() {
        return financialTxn;
    }

    /**
     * @param financialTxn the financialTxn to set
     */
    public void setFinancialTxn(Integer financialTxn) {
        this.financialTxn = financialTxn;
    }
}