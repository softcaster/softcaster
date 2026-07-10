/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public class CTDData {
    public String underlyingIsin = "";
    public List<CashFlow> underliyngCashFlow = null;
    public double cf = 0;
    public LocalDate maturity = null;
    public DaycountBasis accrualDaycount = null;
    public Frequency frequency = null;
    public double cleanSpotPrice = 0;
    public double netBasis = 0;
    public double irr = 0;
}
