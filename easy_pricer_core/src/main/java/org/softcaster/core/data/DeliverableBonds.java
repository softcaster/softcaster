package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "deliverable_bonds")
@SuppressWarnings("PersistenceUnitPresent")

public class DeliverableBonds implements Serializable {

    @Id
    @SequenceGenerator(name = "deliverable_bonds_seq", sequenceName = "deliverable_bonds_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deliverable_bonds_seq")
    @Column(name = "id_deliverable_bonds")
    private Integer idDeliverableBonds;

    /**
     * Scadenza del bond future (BondFutureMasterData): valorizzata da
     * Hibernate tramite il @JoinColumn di BondFutureMasterData.deliverables.
     */
    @Column(name = "master_data", insertable = false, updatable = false)
    private Integer masterData;

    @Column(name = "expiration_date")
    private java.sql.Date expirationDate;

    @Column(name = "isin")
    private String isin;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "coupon_rate")
    private Double couponRate;

    @Column(name = "bond_maturity")
    private java.sql.Date bondMaturity;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "bond_cf")
    private Double bondCf;

    public Integer getIdDeliverableBonds() {
        return idDeliverableBonds;
    }

    public void setIdDeliverableBonds(Integer idDeliverableBonds) {
        this.idDeliverableBonds = idDeliverableBonds;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    public java.sql.Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(java.sql.Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Double getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(Double couponRate) {
        this.couponRate = couponRate;
    }

    public java.sql.Date getBondMaturity() {
        return bondMaturity;
    }

    public void setBondMaturity(java.sql.Date bondMaturity) {
        this.bondMaturity = bondMaturity;
    }

    public Double getBondCf() {
        return bondCf;
    }

    public void setBondCf(Double bondCf) {
        this.bondCf = bondCf;
    }

    /**
     * Uguaglianza per ISIN: il paniere e' un Set dentro la scadenza, dove l'ISIN
     * e' unico (indice ux_deliverable_bonds_isin), e un titolo appena creato
     * non ha ancora l'id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIsin() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DeliverableBonds that = (DeliverableBonds) obj;
        return getIsin().equals(that.getIsin());
    }

    @Override
    public int hashCode() {
        return getIsin() == null ? 0 : isin.hashCode();
    }
}
