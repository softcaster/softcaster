// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;

public class Future_master_data implements IRecord {

    private Integer id_master_data = 0;
    private String isin = "";
    private String description = "";
    private Integer settlement_type = 0;

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

    public Integer getSettlement_type() {
        return settlement_type;
    }

    public void setSettlement_type(Integer settlement_type) {
        this.settlement_type = settlement_type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
