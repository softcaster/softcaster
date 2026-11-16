package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.Hibernate;

/**
 * Opzione OTC su cambio (OFX): sottostante una coppia di cambi, nozionale
 * nella valuta base, strike e premio nella valuta quotata (regolamento).
 * Pricing: Garman-Kohlhagen (europee) o CRR (americane) su spot e curve
 * della coppia.
 */
@Entity
@Table(name = "otc_fx_option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "OtcFxOptionMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode("currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("underlying")
        }
)
public class OtcFxOptionMasterData extends OtcOptionMasterData {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private ForexMasterData underlying;

    @Override
    protected void checkUnderlying() {
        if (!FX_ASSET_CLASS.equals(getAssetClass().getCode())) {
            throw new IllegalStateException("Option " + label() + ": asset class must be " + FX_ASSET_CLASS);
        }
    }

    /**
     * Coppia senza proxy: le valute servono ai calendari.
     */
    public ForexMasterData getPair() {
        return (ForexMasterData) Hibernate.unproxy(underlying);
    }

    @Override
    public ForexMasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(ForexMasterData underlying) {
        this.underlying = underlying;
    }

    @Override
    public List<Currency> getCurrencyList() {
        return getPair().getCurrencyList();
    }
}
