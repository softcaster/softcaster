package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Scadenza di un future money market: testata MmFutureProduct, che porta il
 * sottostante.
 */
@Entity
@Table(name = "mm_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public class MmFutureMasterData extends FutureMasterData {

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "future_product", nullable = false)
    private MmFutureProduct product;

    /**
     * Nuova scadenza della testata, con i valori di default della testata.
     *
     * @param product
     * @return
     */
    public static MmFutureMasterData of(MmFutureProduct product) {
        MmFutureMasterData mfmd = new MmFutureMasterData();
        mfmd.setProduct(product);
        mfmd.copyProductDefaults(product);
        return mfmd;
    }

    @Override
    public MmFutureProduct getProduct() {
        return product;
    }

    public void setProduct(MmFutureProduct product) {
        this.product = product;
    }

    @Override
    protected Class<? extends FutureProduct> productType() {
        return MmFutureProduct.class;
    }

    /**
     * Sottostante, dalla testata.
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
}
