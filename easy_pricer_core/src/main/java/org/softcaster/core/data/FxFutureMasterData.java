package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Scadenza di un future su cambi (M6EZ6): testata FxFutureProduct (M6E), che
 * porta la coppia sottostante.
 *
 * Entity graph "FxFutureMasterData.fullGraph": testata, coppia, valute delle
 * due divise con calendario e festivita'.
 */
@Entity
@Table(name = "fx_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "FxFutureMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode(value = "currency", subgraph = "currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode(value = "product", subgraph = "product")
        },
        subgraphs = {
            @NamedSubgraph(name = "product", attributeNodes = {
                @NamedAttributeNode(value = "underlying", subgraph = "underlying")
            }),
            @NamedSubgraph(name = "underlying", attributeNodes = {
                @NamedAttributeNode(value = "bcy", subgraph = "currency"),
                @NamedAttributeNode(value = "ccy", subgraph = "currency")
            }),
            @NamedSubgraph(name = "currency", attributeNodes = {
                @NamedAttributeNode(value = "calendar", subgraph = "calendar")
            }),
            @NamedSubgraph(name = "calendar", attributeNodes = {
                @NamedAttributeNode("holidays")
            })
        }
)
public class FxFutureMasterData extends FutureMasterData {

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "future_product", nullable = false)
    private FxFutureProduct product;

    /**
     * Nuova scadenza della testata, con i valori di default della testata.
     *
     * @param product
     * @return
     */
    public static FxFutureMasterData of(FxFutureProduct product) {
        FxFutureMasterData ffmd = new FxFutureMasterData();
        ffmd.setProduct(product);
        ffmd.copyProductDefaults(product);
        return ffmd;
    }

    @Override
    public FxFutureProduct getProduct() {
        return product;
    }

    public void setProduct(FxFutureProduct product) {
        this.product = product;
    }

    @Override
    protected Class<? extends FutureProduct> productType() {
        return FxFutureProduct.class;
    }

    /**
     * Coppia sottostante, dalla testata.
     *
     * @return
     */
    public ForexMasterData getUnderlying() {
        return product == null ? null : product.getUnderlying();
    }

    @Override
    public String toString() {
        return getCode();
    }

    @Override
    public List<Currency> getCurrencyList() {
        ForexMasterData underlying = getUnderlying();
        return underlying == null ? null : underlying.getCurrencyList();
    }

    @Override
    public Currency getSettlementCcy() {
        return getCurrency();
    }

    @Override
    public Currency getMasterDataCcy() {
        return getUnderlying().getBcy();
    }
}
