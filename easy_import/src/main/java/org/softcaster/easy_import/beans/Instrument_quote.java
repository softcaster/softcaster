// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;

public class Instrument_quote implements IRecord {

    private Integer id_instrument_quote = 0;
    private Integer master_data = 0;
    private String code = "";
    private Double bid = 0.0;
    private Double ask = 0.0;

    public Integer getId_instrument_quote() {
        return id_instrument_quote;
    }

    public void setId_instrument_quote(Integer id_instrument_quote) {
        this.id_instrument_quote = id_instrument_quote;
    }

    public Integer getMaster_data() {
        return master_data;
    }

    public void setMaster_data(Integer master_data) {
        this.master_data = master_data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
