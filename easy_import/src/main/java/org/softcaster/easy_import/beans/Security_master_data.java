// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;
import java.sql.Date;

public class Security_master_data implements IRecord {

    private Integer id_master_data = 0;
    private String isin = "";
    private String cfi_code = "";
    private String fisn = "";
    private String lei = "";
    private Integer issuer = 0;
    private Double nominal_value = 0.0;
    private Double first_coupon_rate = 0.0;
    private Date first_coupon_payment_date = null;

    public Integer getId_master_data() {
        return id_master_data;
    }

    public void setId_master_data(Integer id_master_data) {
        this.id_master_data = id_master_data;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCfi_code() {
        return cfi_code;
    }

    public void setCfi_code(String cfi_code) {
        this.cfi_code = cfi_code;
    }

    public String getFisn() {
        return fisn;
    }

    public void setFisn(String fisn) {
        this.fisn = fisn;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public Integer getIssuer() {
        return issuer;
    }

    public void setIssuer(Integer issuer) {
        this.issuer = issuer;
    }

    public Double getNominal_value() {
        return nominal_value;
    }

    public void setNominal_value(Double nominal_value) {
        this.nominal_value = nominal_value;
    }

    public Double getFirst_coupon_rate() {
        return first_coupon_rate;
    }

    public void setFirst_coupon_rate(Double first_coupon_rate) {
        this.first_coupon_rate = first_coupon_rate;
    }

    public Date getFirst_coupon_payment_date() {
        return first_coupon_payment_date;
    }

    public void setFirst_coupon_payment_date(Date first_coupon_payment_date) {
        this.first_coupon_payment_date = first_coupon_payment_date;
    }

}
