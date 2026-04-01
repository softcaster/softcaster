/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.xml;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ep
 */
public class ItemBond {

    public String isincode;
    public String description;
    public String currency;
    public double minimumlot;
    public String status;
    public double amount;
    public Date issuedate;
    public double issueprice;
    public Date redemptiondate;
    public double redemptionprice;
    public double couponrate;
    public int couponperiodicity;
    public int ccissuedelta;
    public int cccoupon;
    public double taxrate;
    public List<Coupon> coupons = new ArrayList<>();
}
