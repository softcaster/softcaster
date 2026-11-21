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
import org.softcaster.core.data.converters.CurveRoleConverter;
import org.softcaster.engine.enums.CurveRole;

/**
 * Curva di un emittente per valuta e ruolo (es. REP ITA / EUR / DISCOUNT ->
 * ITA_SPREADED): prevale su ac_dc_bcy_link per i titoli dell'emittente
 * (AcDcBcyLinkDAO.findCurveCode per anagrafica). Esattamente una tra yield
 * curve e spread curve.
 */
@Entity
@Table(name = "issuer_curve_link")
@SuppressWarnings("PersistenceUnitPresent")
public class IssuerCurveLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "issuer_curve_link_seq", sequenceName = "issuer_curve_link_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issuer_curve_link_seq")
    @Column(name = "issuer_curve_link_id")
    private Integer issuerCurveLinkId;

    @Column(name = "issuer", nullable = false)
    private Integer issuer;

    @Column(name = "currency", nullable = false)
    private Integer currency;

    @Convert(converter = CurveRoleConverter.class)
    @Column(name = "curve_role", nullable = false)
    private CurveRole curveRole;

    @Column(name = "yield_curve")
    private Integer yieldCurve;

    @Column(name = "spread_curve")
    private Integer spreadCurve;

    @PrePersist
    @PreUpdate
    private void onSave() {
        if ((yieldCurve == null) == (spreadCurve == null)) {
            throw new IllegalStateException("issuer_curve_link (issuer=" + issuer + ", currency=" + currency
                    + ", role=" + curveRole + "): set exactly one of yield_curve and spread_curve");
        }
    }

    public boolean isSpreadCurve() {
        return spreadCurve != null;
    }

    public Integer getIssuerCurveLinkId() {
        return issuerCurveLinkId;
    }

    public Integer getIssuer() {
        return issuer;
    }

    public void setIssuer(Integer issuer) {
        this.issuer = issuer;
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
}
