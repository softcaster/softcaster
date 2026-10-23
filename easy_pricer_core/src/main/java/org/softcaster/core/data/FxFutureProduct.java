package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Testata di un future su cambi (M6E): coppia sottostante.
 */
@Entity
@Table(name = "fx_future_product")
@PrimaryKeyJoinColumn(name = "id_future_product")
@SuppressWarnings("PersistenceUnitPresent")
public class FxFutureProduct extends FutureProduct {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private ForexMasterData underlying;

    public ForexMasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(ForexMasterData underlying) {
        this.underlying = underlying;
    }
}
