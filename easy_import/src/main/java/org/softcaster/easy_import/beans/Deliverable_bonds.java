// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import java.sql.Date;
import org.softcaster.commons.generator.IRecord;

public class Deliverable_bonds implements IRecord {

    private Integer id_deliverable_bonds = 0;
    private Integer master_data = 0;
    private Date expiration_date = null;
    private String isin = "";
    private Double coupon_rate = 0.0;
    private Date bond_maturity = null;
    private Double bond_cf = 0.0;

    public Integer getId_deliverable_bonds() {
        return id_deliverable_bonds;
    }

    public void setId_deliverable_bonds(Integer id_deliverable_bonds) {
        this.id_deliverable_bonds = id_deliverable_bonds;
    }

    public Integer getMaster_data() {
        return master_data;
    }

    public void setMaster_data(Integer master_data) {
        this.master_data = master_data;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Double getCoupon_rate() {
        return coupon_rate;
    }

    public void setCoupon_rate(Double coupon_rate) {
        this.coupon_rate = coupon_rate;
    }

    public Double getBond_cf() {
        return bond_cf;
    }

    public void setBond_cf(Double bond_cf) {
        this.bond_cf = bond_cf;
    }

    /**
     * @return the expiration_date
     */
    public Date getExpiration_date() {
        return expiration_date;
    }

    /**
     * @param expiration_date the expiration_date to set
     */
    public void setExpiration_date(Date expiration_date) {
        this.expiration_date = expiration_date;
    }

    /**
     * @return the bond_maturity
     */
    public Date getBond_maturity() {
        return bond_maturity;
    }

    /**
     * @param bond_maturity the bond_maturity to set
     */
    public void setBond_maturity(Date bond_maturity) {
        this.bond_maturity = bond_maturity;
    }

}
