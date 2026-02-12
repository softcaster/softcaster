/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xls;

/**
 *
 * @author ep
 */
public class Header {
    
    private String headerID = "";
    private Type headerType = Type.UNDEFINED;

    /**
     * @return the headerID
     */
    public String getHeaderID() {
        return headerID;
    }

    /**
     * @param headerID the headerID to set
     */
    public void setHeaderID(String headerID) {
        this.headerID = headerID;
    }

    /**
     * @return the headerType
     */
    public Type getHeaderType() {
        return headerType;
    }

    /**
     * @param headerType the headerType to set
     */
    public void setHeaderType(Type headerType) {
        this.headerType = headerType;
    }

}
