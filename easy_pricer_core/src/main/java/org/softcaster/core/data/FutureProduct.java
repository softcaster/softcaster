package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.SettlementTypeConverter;
import org.softcaster.engine.enums.SettlementType;

/**
 * Testata di un future (MBTP, M6E): dati comuni a tutte le scadenze.
 *
 * Non e' in master_data: lo strumento negoziabile e' la singola scadenza
 * (FutureMasterData), che punta alla testata. Strategia JOINED, una
 * sottoclasse per tipo di future, simmetrica alle scadenze.
 *
 * currency, assetClass, businessDays e multiplier sono i valori copiati in
 * master_data quando si crea una scadenza.
 */
@Entity
@Table(name = "future_product")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FutureProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Lettere standard dei mesi di scadenza (F = gennaio ... Z = dicembre).
     */
    private static final String MONTH_CODES = "FGHJKMNQUVXZ";

    @Id
    @SequenceGenerator(name = "future_product_seq", sequenceName = "future_product_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "future_product_seq")
    @Column(name = "id_future_product")
    private Integer idFutureProduct;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "exchange_contract_code")
    private String exchangeContractCode;

    @Convert(converter = SettlementTypeConverter.class)
    @Column(name = "settlement_type")
    private SettlementType settlementType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "contract_value")
    private Double contractValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "tick_size")
    private Double tickSize;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "initial_margin")
    private Double initialMargin;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "maintenance_margin")
    private Double maintenanceMargin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_class", nullable = false)
    private AssetClass assetClass;

    @Column(name = "business_days")
    private Integer businessDays;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "multiplier")
    private Double multiplier;

    /**
     * Codice standard di una scadenza: prodotto + lettera del mese + ultima
     * cifra dell'anno (MBTP, dicembre 2026 -> MBTPZ6).
     *
     * @param contractMonth qualunque giorno del mese di scadenza
     * @return
     */
    public String contractCode(java.time.LocalDate contractMonth) {
        return code + MONTH_CODES.charAt(contractMonth.getMonthValue() - 1) + (contractMonth.getYear() % 10);
    }

    public Integer getIdFutureProduct() {
        return idFutureProduct;
    }

    public void setIdFutureProduct(Integer idFutureProduct) {
        this.idFutureProduct = idFutureProduct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExchangeContractCode() {
        return exchangeContractCode;
    }

    public void setExchangeContractCode(String exchangeContractCode) {
        this.exchangeContractCode = exchangeContractCode;
    }

    public SettlementType getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    public Double getContractValue() {
        return contractValue;
    }

    public void setContractValue(Double contractValue) {
        this.contractValue = contractValue;
    }

    public Double getTickSize() {
        return tickSize;
    }

    public void setTickSize(Double tickSize) {
        this.tickSize = tickSize;
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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    public Integer getBusinessDays() {
        return businessDays;
    }

    public void setBusinessDays(Integer businessDays) {
        this.businessDays = businessDays;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdFutureProduct() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FutureProduct that = (FutureProduct) obj;
        return getIdFutureProduct().equals(that.getIdFutureProduct());
    }

    @Override
    public int hashCode() {
        return getIdFutureProduct() == null ? 0 : idFutureProduct.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
