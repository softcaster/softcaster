package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.AccountingPhaseConverter;
import org.softcaster.core.data.converters.TxnSideConverter;
import org.softcaster.core.data.converters.TxnStatusConverter;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;

@Entity
@Table(name = "financial_txn")
@SuppressWarnings("PersistenceUnitPresent")

public class FinancialTxn implements Serializable {

    @Id
    @SequenceGenerator(name = "financial_txn_seq", sequenceName = "financial_txn_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "financial_txn_seq")
    @Column(name = "id_financial_txn", columnDefinition = "INTEGER")
    private Integer idFinancialTxn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty", nullable = true)
    private Counterparty counterparty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_md", nullable = true)
    private PositionMasterData positionMd;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_data", nullable = true)
    private MasterData masterData;

    @Convert(converter = TxnStatusConverter.class)
    @Column(name = "txn_status")
    private TxnStatus txnStatus;

    @Convert(converter = TxnStatusConverter.class)
    @Column(name = "txn_status_pre_elab")
    private TxnStatus txnStatusPreElab;
    
    @Convert(converter = AccountingPhaseConverter.class)
    @Column(name = "txn_acct_phase")
    private AccountingPhase txnAcctPhase;

    @Convert(converter = TxnSideConverter.class)
    @Column(name = "txn_side")
    private TxnSide txnSide;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "financial_txn", nullable = false) // FK in child table holiday
    private List<FinancialTxnComponent> components = new ArrayList<>();

    public void addTxnComponent(FinancialTxnComponent component) {
        components.add(component);
    }
    
    @Column(name = "ref_id")
    private Integer refId;

    @Column(name = "description")
    private String description;

    @Column(name = "trade_date")
    private java.sql.Date tradeDate; // Esecuzione deal

    @Column(name = "value_date")
    private java.sql.Date valueDate; // Data valuta cash

    @Column(name = "settlement")
    private java.sql.Date settlement; // Regolamento contabilizzazione

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "quantity")
    private Double quantity;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "price")
    private Double price;
    
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "fx_rate")
    private Double fxRate;

    @Version
    @Column(name = "version")
    private Integer version;

    public Integer getIdFinancialTxn() {
        return idFinancialTxn;
    }

    public void setIdFinancialTxn(Integer idFinancialTxn) {
        this.idFinancialTxn = idFinancialTxn;
    }

    public TxnSide getTxnSide() {
        return txnSide;
    }

    public void setTxnSide(TxnSide txnSide) {
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

    /**
     * @return the valueDate
     */
    public java.sql.Date getValueDate() {
        return valueDate;
    }

    /**
     * @param valueDate the valueDate to set
     */
    public void setValueDate(java.sql.Date valueDate) {
        this.valueDate = valueDate;
    }

    /**
     * @return the components
     */
    public List<FinancialTxnComponent> getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(List<FinancialTxnComponent> components) {
        this.components = components;
    }

    /**
     * @return the txnStatusPreElab
     */
    public TxnStatus getTxnStatusPreElab() {
        return txnStatusPreElab;
    }

    /**
     * @param txnStatusPreElab the txnStatusPreElab to set
     */
    public void setTxnStatusPreElab(TxnStatus txnStatusPreElab) {
        this.txnStatusPreElab = txnStatusPreElab;
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
}
