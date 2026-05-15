/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cme;

import java.util.ArrayList;

/**
 *
 * @author ep
 */

public class CmeResult {

    public boolean quoteDelayed;
    public String quoteDelay;
    public String tradeDate;
    public ArrayList<Quote> quotes;
    public boolean empty;
}
