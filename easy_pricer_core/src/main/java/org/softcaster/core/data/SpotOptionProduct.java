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
 * Testata di un'opzione su strumento secco (titolo, azione, coppia di
 * cambi). Con underlying valorizzato tutte le serie sono su quello strumento
 * (opzioni Eurex EUR/USD); vuoto per una testata generica (OTC) le cui serie
 * hanno sottostanti diversi.
 */
@Entity
@Table(name = "spot_option_product")
@PrimaryKeyJoinColumn(name = "id_option_product")
@SuppressWarnings("PersistenceUnitPresent")
public class SpotOptionProduct extends OptionProduct {

    private static final long serialVersionUID = 1L;

    /**
     * Famiglie di opzioni su strumento secco (asset class della testata) e
     * tipo di sottostante ammesso. Una nuova famiglia (EQO) va aggiunta qui.
     */
    public static final Map<String, Class<? extends MasterData>> UNDERLYING_TYPES = Map.of(
            "FXO", ForexMasterData.class,
            "BDO", SecurityMasterData.class);

    @ManyToOne(fetch = FetchType.EAGER) // sottostante fisso: letto dalle finestre fuori transazione
    @JoinColumn(name = "underlying")
    private MasterData underlying;

    /**
     * Tipo di sottostante ammesso dalla famiglia della testata.
     *
     * @throws IllegalStateException se l'asset class non e' una famiglia di
     * opzioni su strumento secco
     */
    public Class<? extends MasterData> underlyingType() {
        String family = getAssetClass() == null ? null : getAssetClass().getCode();
        Class<? extends MasterData> type = UNDERLYING_TYPES.get(family);
        if (type == null) {
            throw new IllegalStateException("Testata " + getCode() + ": asset class " + family
                    + " non prevista per le opzioni su strumento secco " + UNDERLYING_TYPES.keySet());
        }
        return type;
    }

    @PrePersist
    @PreUpdate
    private void checkUnderlying() {
        Class<? extends MasterData> type = underlyingType();
        if (underlying != null && !type.isInstance(Hibernate.unproxy(underlying))) {
            throw new IllegalStateException("Testata " + getCode() + ": il sottostante " + underlying.getCode()
                    + " non e' un " + type.getSimpleName() + " (asset class " + getAssetClass().getCode() + ")");
        }
    }

    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
    }
}
