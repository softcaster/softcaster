package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Scadenza di un future su indice (FESXZ6): testata IndexFutureProduct
 * (FESX), che porta l'indice sottostante.
 */
@Entity
@Table(name = "index_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public class IndexFutureMasterData extends FutureMasterData {

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "future_product", nullable = false)
    private IndexFutureProduct product;

    /**
     * Nuova scadenza della testata, con i valori di default della testata.
     *
     * @param product
     * @return
     */
    public static IndexFutureMasterData of(IndexFutureProduct product) {
        IndexFutureMasterData ifmd = new IndexFutureMasterData();
        ifmd.setProduct(product);
        ifmd.copyProductDefaults(product);
        return ifmd;
    }

    @Override
    public IndexFutureProduct getProduct() {
        return product;
    }

    public void setProduct(IndexFutureProduct product) {
        this.product = product;
    }

    @Override
    protected Class<? extends FutureProduct> productType() {
        return IndexFutureProduct.class;
    }

    /**
     * Indice sottostante, dalla testata.
     *
     * @return
     */
    public IndexMasterData getUnderlying() {
        return product == null ? null : product.getUnderlying();
    }

    @Override
    public String toString() {
        return getCode();
    }
}
