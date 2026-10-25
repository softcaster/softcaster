package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;

/**
 * Serie di un'opzione su future (EUUZ6_C1140): il sottostante e' una scadenza
 * del future della testata (M6EZ6). Pricing: Black-76 sul prezzo del future.
 */
@Entity
@Table(name = "future_option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "FutureOptionMasterData.fullGraph",
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
public class FutureOptionMasterData extends OptionMasterData {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private FutureMasterData underlying;

    /**
     * Nuova serie con i default della testata.
     * @param product
     * @return 
     */
    public static FutureOptionMasterData of(FutureOptionProduct product) {
        FutureOptionMasterData fomd = new FutureOptionMasterData();
        fomd.setProduct(product);
        fomd.copyProductDefaults(product);
        return fomd;
    }

    /**
     * Il sottostante deve essere una scadenza del future della testata.
     */
    @Override
    protected void checkUnderlying() {
        FutureMasterData fmd = (FutureMasterData) org.hibernate.Hibernate.unproxy(underlying);
        Integer expected = getProduct().getFutureProduct().getIdFutureProduct();
        if (fmd.getProduct() == null || !expected.equals(fmd.getProduct().getIdFutureProduct())) {
            throw new IllegalStateException("Serie " + getCode() + ": il sottostante " + fmd.getCode()
                    + " non e' una scadenza del future " + getProduct().getFutureProduct().getCode());
        }
    }

    @Override
    protected Class<? extends OptionProduct> productType() {
        return FutureOptionProduct.class;
    }

    @Override
    public FutureOptionProduct getProduct() {
        return (FutureOptionProduct) org.hibernate.Hibernate.unproxy(super.getProduct());
    }

    public void setProduct(FutureOptionProduct product) {
        setOptionProduct(product);
    }

    @Override
    public FutureMasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(FutureMasterData underlying) {
        this.underlying = underlying;
    }
}
