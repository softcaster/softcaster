package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Strumento negoziabile: dati comuni a tutte le anagrafiche.
 *
 * Gerarchia JOINED:
 *   master_data
 *     ├── cash_flow_master_data   strumenti con flussi (convenzioni, prezzi, piano dei flussi)
 *     │     ├── security_master_data -> flt_security_master_data
 *     │     └── loan_master_data
 *     ├── future_master_data      scadenze dei future (testata in future_product)
 *     └── forex_master_data
 */
@Entity
@Table(name = "master_data")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(
        strategy = InheritanceType.JOINED
)

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "MasterData.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass")
            }
    ),

    @NamedEntityGraph(
            name = "MasterData.valuationGraph",
            attributeNodes = {
                @NamedAttributeNode("instrumentValuation")
            }
    )
})
public class MasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "master_data_seq", sequenceName = "master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "master_data_seq")
    @Column(name = "id_master_data")
    private Integer idMasterData;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    // --- RELAZIONE BIDIREZIONALE (LATO INVERSO) ---
    // 1) mappedBy punta al nome del campo Java dentro la classe InstrumentValuation
    @OneToOne(mappedBy = "masterData", fetch = FetchType.LAZY, orphanRemoval = true)
    private InstrumentValuation instrumentValuation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // comunica a jpa/hibernate che la relazione deve esistere
    @JoinColumn(name = "asset_class", nullable = false) // comunica il vincolo sulla colonna DB
    private AssetClass assetClass;

    @Column(name = "issue_date")
    private java.sql.Date issueDate;

    @Column(name = "maturity_date")
    private java.sql.Date maturityDate;

    @Column(name = "business_days")
    private Integer businessDays;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "multiplier")
    private Double multiplier;

    public Integer getIdMasterData() {
        return idMasterData;
    }

    public void setIdMasterData(Integer idMasterData) {
        this.idMasterData = idMasterData;
    }

    public java.sql.Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(java.sql.Date issueDate) {
        this.issueDate = issueDate;
    }

    public java.sql.Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(java.sql.Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Integer getBusinessDays() {
        return businessDays;
    }

    public void setBusinessDays(Integer businessDays) {
        this.businessDays = businessDays;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdMasterData() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MasterData that = (MasterData) obj;
        return getIdMasterData().equals(that.getIdMasterData());
    }

    @Override
    public int hashCode() {
        return getIdMasterData() == null ? 0 : idMasterData.hashCode();
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the assetClass
     */
    public AssetClass getAssetClass() {
        return assetClass;
    }

    /**
     * @param assetClass the assetClass to set
     */
    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the multiplier
     */
    public Double getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier the multiplier to set
     */
    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return the instrumentValuation
     */
    public InstrumentValuation getInstrumentValuation() {
        return instrumentValuation;
    }

    /**
     * @param instrumentValuation the instrumentValuation to set
     */
    public void setInstrumentValuation(InstrumentValuation instrumentValuation) {
        this.instrumentValuation = instrumentValuation;
    }

    public List<Currency> getCurrencyList() {
        List<Currency> currencies = null;
        if (currency != null) {
            currencies = new ArrayList<>();
            currencies.add(currency);
        }

        return currencies;
    }

    public Currency getSettlementCcy() {
        return currency;
    }

    public Currency getMasterDataCcy() {
        return currency;
    }
}
