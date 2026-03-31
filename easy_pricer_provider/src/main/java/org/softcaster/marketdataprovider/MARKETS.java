/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.marketdataprovider;

/**
 *
 * @author ep
 */
public enum MARKETS {
    CURRENCIES,BONDS,EQUITIES,FUTURES,COMMODITIES,YIELDS;
    
    public String display(){
        return "Market is " + this;
    }
}
