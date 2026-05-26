/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bricks;

import java.time.LocalDate;

/**
 *
 * @author ep
 */
public class Node {
  
    private final Offset offset;
    private final String symbol;
    private final String daycount;
    private final String compounding;
    private Data data;
    
    public Node(String symbol, Offset offset, Data data, String daycount, String compounding) {
        this.offset = offset;
        this.data = data;
        this.symbol = symbol;
        this.compounding = compounding;
        this.daycount = daycount;
    }

    public LocalDate maturity(LocalDate officialDate) {
        LocalDate ld = officialDate;
        
        switch(offset.offsetType()) {
            case DAYS -> {
                ld = ld.plusDays(offset.step());
            }
            case MONTHS -> {
                ld = ld.plusMonths(offset.step());
            }
            case YEARS -> {
                ld = ld.plusYears(offset.step());
            }
        }
        return ld;
    }
    
    /**
     * @return the offset
     */
    public Offset getOffset() {
        return offset;
    }

    /**
     * @return the data
     */
    public Data getData() {
        return data;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param data the data to set
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * @return the daycount
     */
    public String getDaycount() {
        return daycount;
    }

    /**
     * @return the compounding
     */
    public String getCompounding() {
        return compounding;
    }
}
