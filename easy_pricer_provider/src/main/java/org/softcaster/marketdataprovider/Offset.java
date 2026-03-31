/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.marketdataprovider;

/**
 *
 * @author svil
 */
public class Offset {
    private int offset = 0;
    private OFFSET_TYPE offsetType = OFFSET_TYPE.DAYS;
    private String isinCode = "";

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return the offsetType
     */
    public OFFSET_TYPE getOffsetType() {
        return offsetType;
    }

    /**
     * @param offsetType the offsetType to set
     */
    public void setOffsetType(OFFSET_TYPE offsetType) {
        this.offsetType = offsetType;
    }

    /**
     * @return the isinCode
     */
    public String getIsinCode() {
        return isinCode;
    }

    /**
     * @param isinCode the isinCode to set
     */
    public void setIsinCode(String isinCode) {
        this.isinCode = isinCode;
    }
    
}
