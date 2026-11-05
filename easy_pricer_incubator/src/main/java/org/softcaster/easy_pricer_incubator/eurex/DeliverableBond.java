/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.eurex;

public record DeliverableBond(int contractId, String bondIsin, String bondName,
        double coupon, String maturity, double conversionFactor) {

}
