/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import org.softcaster.core.data.converters.CurveRoleConverter;
import org.softcaster.engine.enums.CurveRole;

/**
 * Collega una coppia (asset class, valuta) alla curva da usare per un
 * determinato ruolo.
 *
 * La curva e' una yield_curve oppure una spread_curve: esattamente una delle
 * due colonne e' valorizzata (CHECK ck_ac_dc_bcy_one_curve sul database,
 * verificato anche in onSave()).
 *
 * Chiave naturale: (asset_class, currency, curve_role).
 */
@Entity
@Table(name = "ac_dc_bcy_link")
@SuppressWarnings("PersistenceUnitPresent")
public class AcDcBcyLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ac_dc_bcy_link_seq", sequenceName = "ac_dc_bcy_link_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ac_dc_bcy_link_seq")
    @Column(name = "ac_dc_bcy_link_id", columnDefinition = "INTEGER")
    private Integer acDcBcyLinkId;

    @Column(name = "asset_class", nullable = false, columnDefinition = "INTEGER")
    private Integer assetClass;

    @Column(name = "currency", nullable = false, columnDefinition = "INTEGER")
    private Integer currency;

    @Convert(converter = CurveRoleConverter.class)
    @Column(name = "curve_role")
    private CurveRole curveRole;

    /**
     * id_yield_curve, oppure null se la curva e' una spread_curve.
     */
    @Column(name = "yield_curve", columnDefinition = "INTEGER")
    private Integer yieldCurve;

    /**
     * id della spread_curve, oppure null se la curva e' una yield_curve.
     */
    @Column(name = "spread_curve", columnDefinition = "INTEGER")
    private Integer spreadCurve;

    public AcDcBcyLink() {
    }

    // ------------------------------------------------------------------------
    // -- Metodi di comodo
    // ------------------------------------------------------------------------
    /**
     * true se la curva collegata e' una spread_curve.
     */
    public boolean isSpreadCurve() {
        return spreadCurve != null;
    }

    /**
     * Id della curva collegata, qualunque sia il tipo: usare insieme a
     * isSpreadCurve().
     */
    public Integer getCurveId() {
        return spreadCurve != null ? spreadCurve : yieldCurve;
    }

    /**
     * Collega una yield_curve (azzera l'eventuale spread_curve).
     */
    public void linkYieldCurve(Integer idYieldCurve) {
        this.yieldCurve = Objects.requireNonNull(idYieldCurve, "idYieldCurve");
        this.spreadCurve = null;
    }

    /**
     * Collega una spread_curve (azzera l'eventuale yield_curve).
     */
    public void linkSpreadCurve(Integer idSpreadCurve) {
        this.spreadCurve = Objects.requireNonNull(idSpreadCurve, "idSpreadCurve");
        this.yieldCurve = null;
    }

    /**
     * Stesso controllo del CHECK sul database, con un messaggio piu' chiaro.
     */
    @PrePersist
    @PreUpdate
    private void onSave() {
        if ((yieldCurve == null) == (spreadCurve == null)) {
            throw new IllegalStateException("ac_dc_bcy_link (asset_class=" + assetClass + ", currency="
                    + currency + ", role=" + curveRole + "): valorizzare esattamente una tra yield_curve e spread_curve");
        }
    }

    // ------------------------------------------------------------------------
    // -- Getter / setter
    // ------------------------------------------------------------------------
    public Integer getAcDcBcyLinkId() {
        return acDcBcyLinkId;
    }

    public void setAcDcBcyLinkId(Integer acDcBcyLinkId) {
        this.acDcBcyLinkId = acDcBcyLinkId;
    }

    public Integer getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(Integer assetClass) {
        this.assetClass = assetClass;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public CurveRole getCurveRole() {
        return curveRole;
    }

    public void setCurveRole(CurveRole curveRole) {
        this.curveRole = curveRole;
    }

    public Integer getYieldCurve() {
        return yieldCurve;
    }

    public void setYieldCurve(Integer yieldCurve) {
        this.yieldCurve = yieldCurve;
    }

    public Integer getSpreadCurve() {
        return spreadCurve;
    }

    public void setSpreadCurve(Integer spreadCurve) {
        this.spreadCurve = spreadCurve;
    }

    // ------------------------------------------------------------------------
    // -- equals / hashCode sulla chiave naturale
    // ------------------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AcDcBcyLink other)) {
            return false;
        }
        return Objects.equals(assetClass, other.assetClass)
                && Objects.equals(currency, other.currency)
                && curveRole == other.curveRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetClass, currency, curveRole);
    }

    @Override
    public String toString() {
        return "AcDcBcyLink[" + acDcBcyLinkId + ": assetClass=" + assetClass + ", currency=" + currency
                + ", role=" + curveRole + ", " + (isSpreadCurve() ? "spreadCurve=" : "yieldCurve=") + getCurveId() + "]";
    }
}
