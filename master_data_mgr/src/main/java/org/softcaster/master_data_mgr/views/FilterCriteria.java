/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.views;

/**
 *
 * @author ep
 */
public class FilterCriteria {
    
    private String isinContains = "";
    private java.sql.Date maturityGE = null;
    private java.sql.Date maturityLE = null;

    /**
     * @return the isinContains
     */
    public String getIsinContains() {
        return isinContains;
    }

    /**
     * @param isinContains the isinContains to set
     */
    public void setIsinContains(String isinContains) {
        this.isinContains = isinContains;
    }

    /**
     * @return the maturityGE
     */
    public java.sql.Date getMaturityGE() {
        return maturityGE;
    }

    /**
     * @param maturityGE the maturityGE to set
     */
    public void setMaturityGE(java.sql.Date maturityGE) {
        this.maturityGE = maturityGE;
    }

    /**
     * @return the maturityLE
     */
    public java.sql.Date getMaturityLE() {
        return maturityLE;
    }

    /**
     * @param maturityLE the maturityLE to set
     */
    public void setMaturityLE(java.sql.Date maturityLE) {
        this.maturityLE = maturityLE;
    }
    
    
}
