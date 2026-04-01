// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;

public class Yield_curve_item implements IRecord {

    private Integer id_yield_curve_item = 0;
    private Integer yield_curve = 0;
    private String ric = "";
    private Integer offset_type = 0;
    private Integer offset_value = 0;
    private Double bid = 0.0;
    private Double ask = 0.0;

    public Integer getId_yield_curve_item() {
        return id_yield_curve_item;
    }

    public void setId_yield_curve_item(Integer id_yield_curve_item) {
        this.id_yield_curve_item = id_yield_curve_item;
    }

    public Integer getYield_curve() {
        return yield_curve;
    }

    public void setYield_curve(Integer yield_curve) {
        this.yield_curve = yield_curve;
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public Integer getOffset_type() {
        return offset_type;
    }

    public void setOffset_type(Integer offset_type) {
        this.offset_type = offset_type;
    }

    public Integer getOffset_value() {
        return offset_value;
    }

    public void setOffset_value(Integer offset_value) {
        this.offset_value = offset_value;
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
