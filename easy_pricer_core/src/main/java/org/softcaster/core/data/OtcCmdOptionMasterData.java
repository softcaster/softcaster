package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;

/**
 * Opzione OTC su commodity (OCM): sottostante una scadenza di future su
 * commodity, nozionale nelle unita' della commodity, strike e premio nella
 * valuta del future. Pricing: Black-76 (europee) o CRR (americane) sul
 * prezzo del future; premio scontato con la curva DISCOUNT di
 * ac_dc_bcy_link per (OCM, valuta) o indicata. Consegna fisica: future al
 * prezzo di mercato, nozionale / contract value contratti.
 */
@Entity
@Table(name = "otc_cmd_option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "OtcCmdOptionMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode("currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("underlying")
        }
)
public class OtcCmdOptionMasterData extends OtcOptionMasterData {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private CmdFutureMasterData underlying;

    @Override
    protected void checkUnderlying() {
        if (!COMMODITY_ASSET_CLASS.equals(getAssetClass().getCode())) {
            throw new IllegalStateException("Option " + label() + ": asset class must be " + COMMODITY_ASSET_CLASS);
        }
        // l'opzione deve scadere entro l'ultimo giorno di negoziazione del future
        CmdFutureMasterData future = getFuture();
        java.time.LocalDate last = future.getLastTradingLocalDate() != null ? future.getLastTradingLocalDate()
                : future.getMaturityDate() == null ? null : future.getMaturityDate().toLocalDate();
        if (last != null && getExpiryLocalDate() != null && getExpiryLocalDate().isAfter(last)) {
            throw new IllegalStateException("Option " + label() + ": expiry after the last trading day of "
                    + future.getCode() + " (" + last + ")");
        }
    }

    public CmdFutureMasterData getFuture() {
        return (CmdFutureMasterData) Hibernate.unproxy(underlying);
    }

    @Override
    public CmdFutureMasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(CmdFutureMasterData underlying) {
        this.underlying = underlying;
    }
}
