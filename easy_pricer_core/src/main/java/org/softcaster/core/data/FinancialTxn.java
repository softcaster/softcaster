package org.softcaster.core.data;

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
import jakarta.persistence.Version;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "financial_txn")
@SuppressWarnings("PersistenceUnitPresent")

public class FinancialTxn implements Serializable {

    @Id
    @SequenceGenerator(name = "financial_txn_seq", sequenceName = "financial_txn_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "financial_txn_seq")
    @Column(name = "id_financial_txn", columnDefinition = "INTEGER")
    private Integer idFinancialTxn;

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

    @Column(name = "ref_id")
    private Integer refId;

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

    @Version
    private Integer version;

    public Integer getIdFinancialTxn() {
        return idFinancialTxn;
    }

    public void setIdFinancialTxn(Integer idFinancialTxn) {
        this.idFinancialTxn = idFinancialTxn;
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
        if (getIdFinancialTxn() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FinancialTxn that = (FinancialTxn) obj;
        return getIdFinancialTxn().equals(that.getIdFinancialTxn());
    }

    @Override
    public int hashCode() {
        return getIdFinancialTxn() == null ? 0 : idFinancialTxn.hashCode();
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

    /**
     * @return the refId
     */
    public Integer getRefId() {
        return refId;
    }

    /**
     * @param refId the refId to set
     */
    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
}
