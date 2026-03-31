package org.softcaster.marketdataprovider;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.StringTokenizer;

/**
 *
 * @author ep
 */
public class YieldNode extends DataNode {

    private OFFSET_TYPE offsetType = OFFSET_TYPE.DAYS;
    private int offset;
    private org.softcaster.commons.types.Date maturity;

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
     * @return the maturity
     */
    public org.softcaster.commons.types.Date getMaturity() {
        return maturity;
    }

    /**
     * @param maturity the maturity to set
     */
    public void setMaturity(org.softcaster.commons.types.Date maturity) {
        this.maturity = maturity;
    }

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

    @Override
    public String toString() {
        StringTokenizer st = new StringTokenizer(getRic(), "-");
        String _ric = "";
        int count = 0;
        while (st.hasMoreTokens()) {
            if (count <= 2) {
                _ric += st.nextToken() + " ";
                count++;
            } else {
                break;
            }
        }
        
        String data = _ric + " maturity: " + maturity + " bid:" + getRic();
        return data;
    }
}
