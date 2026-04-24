package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "finacial_txn")
@SuppressWarnings("PersistenceUnitPresent")

public class FinacialTxn implements Serializable {

    @Id
    @SequenceGenerator(name = "finacial_txn_seq", sequenceName = "finacial_txn_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "finacial_txn_seq")
    @Column(name = "id_finacial_txn", columnDefinition = "INTEGER")
    private Integer idFinacialTxn;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "counterparty", nullable = true)
    private Counterparty counterparty;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_md", nullable = true)
    private PositionMasterData positionMd;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "master_data", nullable = true)
    private MasterData masterData;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "txn_status", nullable = true)
    private TxnStatus txnStatus;

    @Column(name = "txn_side")
    private Short txnSide;

    @Column(name = "description")
    private String description;

    @Column(name = "trade_date")
    private java.sql.Date tradeDate;

    @Column(name = "settlement")
    private java.sql.Date settlement;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "quantity")
    private Double quantity;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "price")
    private Double price;

    public Integer getIdFinacialTxn() {
        return idFinacialTxn;
    }

    public void setIdFinacialTxn(Integer idFinacialTxn) {
        this.idFinacialTxn = idFinacialTxn;
    }

    public Short getTxnSide() {
        return txnSide;
    }

    public void setTxnSide(Short txnSide) {
        this.txnSide = txnSide;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.sql.Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(java.sql.Date tradeDate) {
        this.tradeDate = tradeDate;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdFinacialTxn() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FinacialTxn that = (FinacialTxn) obj;
        return getIdFinacialTxn().equals(that.getIdFinacialTxn());
    }

    @Override
    public int hashCode() {
        return getIdFinacialTxn() == null ? 0 : idFinacialTxn.hashCode();
    }

    /**
     * @return the counterparty
     */
    public Counterparty getCounterparty() {
        return counterparty;
    }

    /**
     * @param counterparty the counterparty to set
     */
    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    /**
     * @return the positionMd
     */
    public PositionMasterData getPositionMd() {
        return positionMd;
    }

    /**
     * @param positionMd the positionMd to set
     */
    public void setPositionMd(PositionMasterData positionMd) {
        this.positionMd = positionMd;
    }

    /**
     * @return the masterData
     */
    public MasterData getMasterData() {
        return masterData;
    }

    /**
     * @param masterData the masterData to set
     */
    public void setMasterData(MasterData masterData) {
        this.masterData = masterData;
    }

    /**
     * @return the txnStatus
     */
    public TxnStatus getTxnStatus() {
        return txnStatus;
    }

    /**
     * @param txnStatus the txnStatus to set
     */
    public void setTxnStatus(TxnStatus txnStatus) {
        this.txnStatus = txnStatus;
    }
}
