package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.OptionTypeConverter;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.enums.PremiumStyle;
import org.softcaster.engine.enums.SettlementType;

/**
 * Serie negoziabile di un'opzione: una riga di master_data come le scadenze
 * dei future. La scadenza e' maturity_date; orario e fuso sono della testata.
 *
 * Due rami per il sottostante (lo strumento su cui si esercita):
 * - FutureOptionMasterData: una scadenza di future (EUUZ6_C1140 su M6EZ6);
 * - SpotOptionMasterData: uno strumento secco (titolo, azione, coppia di
 *   cambi), con la data di consegna.
 * Il ramo sceglie il modello di pricing.
 */
@Entity
@Table(name = "option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class OptionMasterData extends MasterData {

    // testata nella tabella base; ogni ramo la restituisce con il proprio tipo
    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "option_product", nullable = false)
    private OptionProduct product;

    @Convert(converter = OptionTypeConverter.class)
    @Column(name = "option_type", nullable = false)
    private OptionType optionType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "strike", nullable = false)
    private Double strike;

    @Column(name = "isin")
    private String isin;

    public OptionProduct getProduct() {
        return product;
    }

    protected void setOptionProduct(OptionProduct product) {
        this.product = product;
    }

    /**
     * Strumento su cui si esercita.
     * @return 
     */
    public abstract MasterData getUnderlying();

    protected abstract Class<? extends OptionProduct> productType();

    /**
     * Coerenza del sottostante con la testata, propria di ogni ramo.
     */
    protected abstract void checkUnderlying();

    @PrePersist
    @PreUpdate
    private void checkSeries() {
        Objects.requireNonNull(product, "Serie " + getCode() + ": testata mancante");
        Objects.requireNonNull(getUnderlying(), "Serie " + getCode() + ": sottostante mancante");
        Object header = Hibernate.unproxy(product);
        if (!productType().isInstance(header)) {
            throw new IllegalStateException("Serie " + getCode() + ": testata " + header
                    + " non e' un " + productType().getSimpleName());
        }
        checkUnderlying();
    }

    /**
     * Default della testata (valuta, asset class, giorni di regolamento,
     * multiplier) per una serie nuova.
     * @param product
     */
    protected void copyProductDefaults(OptionProduct product) {
        setCurrency(product.getCurrency());
        setAssetClass(product.getAssetClass());
        setBusinessDays(product.getBusinessDays());
        setMultiplier(product.getMultiplier());
    }

    public LocalDate getExpiryLocalDate() {
        return getMaturityDate() == null ? null : getMaturityDate().toLocalDate();
    }

    /**
     * Istante di scadenza (data della serie, orario e fuso della testata).
     * @return 
     */
    public ZonedDateTime getExpiryInstant() {
        return getProduct().expiryInstant(getExpiryLocalDate());
    }

    public ExerciseStyle getExerciseStyle() {
        return getProduct().getExerciseStyle();
    }

    public PremiumStyle getPremiumStyle() {
        return getProduct().getPremiumStyle();
    }

    public SettlementType getSettlementType() {
        return getProduct().getSettlementType();
    }

    public Double getContractValue() {
        return getProduct().getContractValue();
    }

    public Double getTickSize() {
        return getProduct().getTickSize();
    }

    public Double getInitialMargin() {
        return getProduct().getInitialMargin();
    }

    public Double getMaintenanceMargin() {
        return getProduct().getMaintenanceMargin();
    }

    public String getExchangeContractCode() {
        return getProduct().getExchangeContractCode();
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
