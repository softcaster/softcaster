// File generato automaticamente. Non modificare!
package org.softcaster.easy_pricer;

import org.softcaster.commons.generator.IRecord;

public class Currency_pair implements IRecord {

    private Integer id_currency_pair = 0;
    private String code = "";
    private Integer bcy = 0;
    private Integer ccy = 0;
    private Double bid = 0.0;
    private Double ask = 0.0;

    public Integer getId_currency_pair() {
        return id_currency_pair;
    }

    public void setId_currency_pair(Integer id_currency_pair) {
        this.id_currency_pair = id_currency_pair;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getBcy() {
        return bcy;
    }

    public void setBcy(Integer bcy) {
        this.bcy = bcy;
    }

    public Integer getCcy() {
        return ccy;
    }

    public void setCcy(Integer ccy) {
        this.ccy = ccy;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

}
