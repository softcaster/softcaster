package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.ExerciseStyleConverter;
import org.softcaster.core.data.converters.PremiumStyleConverter;
import org.softcaster.core.data.converters.SettlementTypeConverter;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.PremiumStyle;
import org.softcaster.engine.enums.SettlementType;

/**
 * Testata di un'opzione quotata (EUU): dati comuni a tutte le serie, fuori da
 * master_data come le testate dei future.
 *
 * Sottostante (al piu' uno dei due):
 * - futureProduct: opzione su future, ogni serie punta a una scadenza di
 *   questa testata (le mensili esercitano nella trimestrale piu' vicina);
 * - underlying: opzione su sottostante secco fisso (titolo, azione, coppia di
 *   cambi), tutte le serie puntano a questo strumento.
 */
@Entity
@Table(name = "option_product")
@SuppressWarnings("PersistenceUnitPresent")
public class OptionProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "option_product_seq", sequenceName = "option_product_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_product_seq")
    @Column(name = "id_option_product")
    private Integer idOptionProduct;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "exchange_contract_code")
    private String exchangeContractCode;

    @Convert(converter = ExerciseStyleConverter.class)
    @Column(name = "exercise_style", nullable = false)
    private ExerciseStyle exerciseStyle;

    @Convert(converter = PremiumStyleConverter.class)
    @Column(name = "premium_style", nullable = false)
    private PremiumStyle premiumStyle;

    @Convert(converter = SettlementTypeConverter.class)
    @Column(name = "settlement_type", nullable = false)
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

    @Column(name = "expiry_time")
    private LocalTime expiryTime;

    @Column(name = "expiry_time_zone")
    private String expiryTimeZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "future_product")
    private FutureProduct futureProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "underlying")
    private MasterData underlying;

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

    @PrePersist
    @PreUpdate
    private void checkUnderlying() {
        if (futureProduct != null && underlying != null) {
            throw new IllegalStateException("Testata " + code
                    + ": indicare il future sottostante oppure il sottostante secco, non entrambi");
        }
    }

    /**
     * true se il sottostante e' un future (Black-76 sul prezzo del future).
     */
    public boolean isOnFuture() {
        return futureProduct != null;
    }

    /**
     * Istante di scadenza di una serie con scadenza nel giorno indicato;
     * senza orario o fuso: fine giornata UTC.
     */
    public ZonedDateTime expiryInstant(LocalDate expiryDate) {
        ZoneId zone = expiryTimeZone == null || expiryTimeZone.isBlank() ? ZoneId.of("UTC") : ZoneId.of(expiryTimeZone);
        LocalTime time = expiryTime == null ? LocalTime.MAX : expiryTime;
        return ZonedDateTime.of(expiryDate, time, zone);
    }

    public Integer getIdOptionProduct() {
        return idOptionProduct;
    }

    public void setIdOptionProduct(Integer idOptionProduct) {
        this.idOptionProduct = idOptionProduct;
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

    public ExerciseStyle getExerciseStyle() {
        return exerciseStyle;
    }

    public void setExerciseStyle(ExerciseStyle exerciseStyle) {
        this.exerciseStyle = exerciseStyle;
    }

    public PremiumStyle getPremiumStyle() {
        return premiumStyle;
    }

    public void setPremiumStyle(PremiumStyle premiumStyle) {
        this.premiumStyle = premiumStyle;
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

    public LocalTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getExpiryTimeZone() {
        return expiryTimeZone;
    }

    public void setExpiryTimeZone(String expiryTimeZone) {
        this.expiryTimeZone = expiryTimeZone;
    }

    public FutureProduct getFutureProduct() {
        return futureProduct;
    }

    public void setFutureProduct(FutureProduct futureProduct) {
        this.futureProduct = futureProduct;
    }

    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
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
        if (getIdOptionProduct() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OptionProduct that = (OptionProduct) obj;
        return getIdOptionProduct().equals(that.getIdOptionProduct());
    }

    @Override
    public int hashCode() {
        return getIdOptionProduct() == null ? 0 : idOptionProduct.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
