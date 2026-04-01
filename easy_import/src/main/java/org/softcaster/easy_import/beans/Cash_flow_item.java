// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import java.sql.Date;

import org.softcaster.commons.generator.IRecord;

public class Cash_flow_item implements IRecord {

    private Integer id_cash_flow_item = 0;
    private Integer master_data = 0;
    private Date start_date = null;
    private Date end_date = null;
    private Double interest = 0.0;
    private Double amount = 0.0;

    public Integer getId_cash_flow_item() {
        return id_cash_flow_item;
    }

    public void setId_cash_flow_item(Integer id_cash_flow_item) {
        this.id_cash_flow_item = id_cash_flow_item;
    }

    public Integer getMaster_data() {
        return master_data;
    }

    public void setMaster_data(Integer master_data) {
        this.master_data = master_data;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
