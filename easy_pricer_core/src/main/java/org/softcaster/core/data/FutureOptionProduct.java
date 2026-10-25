package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.Hibernate;

/**
 * Testata di un'opzione su future (EUU su M6E): ogni serie punta a una
 * scadenza del future della testata (le mensili esercitano nella trimestrale
 * piu' vicina).
 */
@Entity
@Table(name = "future_option_product")
@PrimaryKeyJoinColumn(name = "id_option_product")
@SuppressWarnings("PersistenceUnitPresent")
public class FutureOptionProduct extends OptionProduct {

    private static final long serialVersionUID = 1L;

    /**
     * Famiglie di opzioni su future (asset class della testata) e tipo di
     * testata del future sottostante ammesso. Una nuova famiglia va aggiunta
     * qui.
     */
    public static final Map<String, Class<? extends FutureProduct>> FUTURE_PRODUCT_TYPES = Map.of(
            "FFO", FxFutureProduct.class,
            "BFO", BondFutureProduct.class);

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata del future: poche righe, letta dalle finestre
    @JoinColumn(name = "future_product", nullable = false)
    private FutureProduct futureProduct;

    /**
     * Il future sottostante deve essere del tipo previsto dalla famiglia
     * (FFO: FX future, BFO: bond future).
     */
    @PrePersist
    @PreUpdate
    private void checkFutureProduct() {
        String family = getAssetClass() == null ? null : getAssetClass().getCode();
        Class<? extends FutureProduct> expected = FUTURE_PRODUCT_TYPES.get(family);
        if (expected == null) {
            throw new IllegalStateException("Testata " + getCode() + ": asset class " + family
                    + " non prevista per le opzioni su future " + FUTURE_PRODUCT_TYPES.keySet());
        }
        if (!expected.isInstance(Hibernate.unproxy(futureProduct))) {
            throw new IllegalStateException("Testata " + getCode() + ": il future sottostante "
                    + futureProduct + " non e' un " + expected.getSimpleName() + " (asset class " + family + ")");
        }
    }

    public FutureProduct getFutureProduct() {
        return futureProduct;
    }

    public void setFutureProduct(FutureProduct futureProduct) {
        this.futureProduct = futureProduct;
    }
}
