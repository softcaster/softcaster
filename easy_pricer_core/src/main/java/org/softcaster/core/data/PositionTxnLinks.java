package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.AccountingPhaseConverter;
import org.softcaster.engine.enums.AccountingPhase;

@Entity
@Table(name = "position_txn_links")
@SuppressWarnings("PersistenceUnitPresent")

public class PositionTxnLinks implements Serializable {

    @Id
    @SequenceGenerator(name = "position_txn_links_seq", sequenceName = "position_txn_links_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_txn_links_seq")
    @Column(name = "pos_txn_link_id")
    private Integer posTxnLinkId;

    @Column(name = "position_detail")
    private Integer positionDetail;

    @Column(name = "financial_txn")
    private Integer financialTxn;

    @Convert(converter = AccountingPhaseConverter.class)
    @Column(name = "txn_acct_phase")
    private AccountingPhase txnAcctPhase;

    @Column(name = "settlement")
    private java.sql.Date settlement;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "quantity")
    private Double quantity;
    
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "price")    
    private Double price;
    
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "fx_rate")
    private Double fxRate;

    public Integer getPosTxnLinkId() {
        return posTxnLinkId;
    }

    public void setPosTxnLinkId(Integer posTxnLinkId) {
        this.posTxnLinkId = posTxnLinkId;
    }

    public Integer getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(Integer positionDetail) {
        this.positionDetail = positionDetail;
    }

    public Integer getFinancialTxn() {
        return financialTxn;
    }

    public void setFinancialTxn(Integer financialTxn) {
        this.financialTxn = financialTxn;
    }

    public java.sql.Date getSettlement() {
        return settlement;
    }

    public void setSettlement(java.sql.Date settlement) {
        this.settlement = settlement;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PositionTxnLinks that)) {
            return false;
        }

        return posTxnLinkId != null
                && posTxnLinkId.equals(that.posTxnLinkId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the txnAcctPhase
     */
    public AccountingPhase getTxnAcctPhase() {
        return txnAcctPhase;
    }

    /**
     * @param txnAcctPhase the txnAcctPhase to set
     */
    public void setTxnAcctPhase(AccountingPhase txnAcctPhase) {
        this.txnAcctPhase = txnAcctPhase;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the fxRate
     */
    public Double getFxRate() {
        return fxRate;
    }

    /**
     * @param fxRate the fxRate to set
     */
    public void setFxRate(Double fxRate) {
        this.fxRate = fxRate;
    }
}
