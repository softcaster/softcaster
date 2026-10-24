package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
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
 * Serie negoziabile di un'opzione (EUUZ6 C1.135): una riga di master_data
 * come le scadenze dei future. La scadenza e' maturity_date; orario e fuso
 * sono della testata.
 *
 * underlying e' lo strumento su cui si esercita: una scadenza di future
 * (opzione su future) oppure un sottostante secco (titolo, azione, coppia di
 * cambi). Il tipo del sottostante sceglie il modello di pricing.
 */
@Entity
@Table(name = "option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "OptionMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode("currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode(value = "product", subgraph = "product"),
            @NamedAttributeNode("underlying")
        },
        subgraphs = {
            @NamedSubgraph(name = "product", attributeNodes = {
                @NamedAttributeNode("futureProduct"),
                @NamedAttributeNode("underlying"),
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass")
            })
        }
)
public class OptionMasterData extends MasterData {

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "option_product", nullable = false)
    private OptionProduct product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private MasterData underlying;

    @Convert(converter = OptionTypeConverter.class)
    @Column(name = "option_type", nullable = false)
    private OptionType optionType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "strike", nullable = false)
    private Double strike;

    @Column(name = "isin")
    private String isin;

    /**
     * Nuova serie con i default della testata (valuta, asset class, giorni di
     * regolamento, multiplier) e, per un sottostante secco fisso, il suo
     * sottostante.
     */
    public static OptionMasterData of(OptionProduct product) {
        OptionMasterData omd = new OptionMasterData();
        omd.setProduct(product);
        omd.setCurrency(product.getCurrency());
        omd.setAssetClass(product.getAssetClass());
        omd.setBusinessDays(product.getBusinessDays());
        omd.setMultiplier(product.getMultiplier());
        if (product.getUnderlying() != null) {
            omd.setUnderlying(product.getUnderlying());
        }
        return omd;
    }

    /**
     * Il sottostante deve essere una scadenza del future della testata,
     * oppure il sottostante secco fisso della testata.
     */
    @PrePersist
    @PreUpdate
    private void checkUnderlying() {
        Objects.requireNonNull(product, "Serie " + getCode() + ": testata mancante");
        Objects.requireNonNull(underlying, "Serie " + getCode() + ": sottostante mancante");
        if (product.getFutureProduct() != null) {
            Object md = Hibernate.unproxy(underlying);
            Integer expected = product.getFutureProduct().getIdFutureProduct();
            if (!(md instanceof FutureMasterData fmd) || fmd.getProduct() == null
                    || !expected.equals(fmd.getProduct().getIdFutureProduct())) {
                throw new IllegalStateException("Serie " + getCode() + ": il sottostante " + underlying.getCode()
                        + " non e' una scadenza del future " + product.getFutureProduct().getCode());
            }
        } else if (product.getUnderlying() != null
                && !product.getUnderlying().getIdMasterData().equals(underlying.getIdMasterData())) {
            throw new IllegalStateException("Serie " + getCode() + ": il sottostante deve essere "
                    + product.getUnderlying().getCode());
        }
    }

    public boolean isOnFuture() {
        return product != null && product.isOnFuture();
    }

    public LocalDate getExpiryLocalDate() {
        return getMaturityDate() == null ? null : getMaturityDate().toLocalDate();
    }

    /**
     * Istante di scadenza (data della serie, orario e fuso della testata).
     */
    public ZonedDateTime getExpiryInstant() {
        return product.expiryInstant(getExpiryLocalDate());
    }

    public ExerciseStyle getExerciseStyle() {
        return product.getExerciseStyle();
    }

    public PremiumStyle getPremiumStyle() {
        return product.getPremiumStyle();
    }

    public SettlementType getSettlementType() {
        return product.getSettlementType();
    }

    public Double getContractValue() {
        return product.getContractValue();
    }

    public Double getTickSize() {
        return product.getTickSize();
    }

    public Double getInitialMargin() {
        return product.getInitialMargin();
    }

    public Double getMaintenanceMargin() {
        return product.getMaintenanceMargin();
    }

    public String getExchangeContractCode() {
        return product.getExchangeContractCode();
    }

    public OptionProduct getProduct() {
        return product;
    }

    public void setProduct(OptionProduct product) {
        this.product = product;
    }

    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
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
