package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Serie di un'opzione su strumento secco (titolo, azione, coppia di cambi).
 * deliveryDate: consegna del sottostante (per le opzioni FX scadenza + spot
 * lag). Pricing: Garman-Kohlhagen (cambi), Black-Scholes (azioni), Black-76
 * sul forward (titoli).
 */
@Entity
@Table(name = "spot_option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "SpotOptionMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode("currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode(value = "product", subgraph = "product"),
            @NamedAttributeNode("underlying")
        },
        subgraphs = {
            @NamedSubgraph(name = "product", attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass")
            })
        }
)
public class SpotOptionMasterData extends OptionMasterData {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private MasterData underlying;

    @Column(name = "delivery_date")
    private java.sql.Date deliveryDate;

    /**
     * Nuova serie con i default della testata e il suo eventuale sottostante
     * fisso.
     */
    public static SpotOptionMasterData of(SpotOptionProduct product) {
        SpotOptionMasterData somd = new SpotOptionMasterData();
        somd.setProduct(product);
        somd.copyProductDefaults(product);
        somd.setUnderlying(product.getUnderlying());
        return somd;
    }

    /**
     * Il sottostante deve essere del tipo della famiglia (FXO: coppia di
     * cambi, BDO: titolo) e, con un sottostante fisso sulla testata, proprio
     * quello.
     */
    @Override
    protected void checkUnderlying() {
        Class<? extends MasterData> type = getProduct().underlyingType();
        if (!type.isInstance(org.hibernate.Hibernate.unproxy(underlying))) {
            throw new IllegalStateException("Serie " + getCode() + ": il sottostante " + underlying.getCode()
                    + " non e' un " + type.getSimpleName());
        }
        MasterData fixed = getProduct().getUnderlying();
        if (fixed != null && !fixed.getIdMasterData().equals(underlying.getIdMasterData())) {
            throw new IllegalStateException("Serie " + getCode() + ": il sottostante deve essere " + fixed.getCode());
        }
    }

    @Override
    protected Class<? extends OptionProduct> productType() {
        return SpotOptionProduct.class;
    }

    @Override
    public SpotOptionProduct getProduct() {
        return (SpotOptionProduct) org.hibernate.Hibernate.unproxy(super.getProduct());
    }

    public void setProduct(SpotOptionProduct product) {
        setOptionProduct(product);
    }

    @Override
    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
    }

    public java.sql.Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(java.sql.Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDate getDeliveryLocalDate() {
        return deliveryDate == null ? null : deliveryDate.toLocalDate();
    }
}
