package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;
import org.softcaster.engine.enums.CommodityType;

/**
 * Scadenza di un future su commodity: testata CmdFutureProduct (o una sua
 * sottoclasse, es. PowerFutureProduct per PowerFutureMasterData).
 */
@Entity
@Table(name = "cmd_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public class CmdFutureMasterData extends FutureMasterData {

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "future_product", nullable = false)
    private CmdFutureProduct product;

    /**
     * Nuova scadenza della testata, con i valori di default della testata.
     *
     * @param product
     * @return
     */
    public static CmdFutureMasterData of(CmdFutureProduct product) {
        CmdFutureMasterData cfmd = new CmdFutureMasterData();
        cfmd.setProduct(product);
        cfmd.copyProductDefaults(product);
        return cfmd;
    }

    @Override
    public CmdFutureProduct getProduct() {
        return product;
    }

    public void setProduct(CmdFutureProduct product) {
        this.product = product;
    }

    @Override
    protected Class<? extends FutureProduct> productType() {
        return CmdFutureProduct.class;
    }

    /**
     * Testata del tipo atteso (es. PowerFutureProduct).
     *
     * @param <T>
     * @param type
     * @return
     */
    protected <T extends CmdFutureProduct> T getProduct(Class<T> type) {
        return type.cast(Hibernate.unproxy(product));
    }

    /**
     * Tipo di commodity, dalla testata.
     *
     * @return
     */
    public CommodityType getCommodityType() {
        return product == null ? null : product.getCommodityType();
    }

    @Override
    public String toString() {
        return getCode();
    }
}
