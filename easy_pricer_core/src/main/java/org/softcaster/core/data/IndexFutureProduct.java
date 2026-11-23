package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Testata di un future su indice azionario (FESX): indice sottostante.
 */
@Entity
@Table(name = "index_future_product")
@PrimaryKeyJoinColumn(name = "id_future_product")
@SuppressWarnings("PersistenceUnitPresent")
public class IndexFutureProduct extends FutureProduct {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private IndexMasterData underlying;

    public IndexMasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(IndexMasterData underlying) {
        this.underlying = underlying;
    }
}
