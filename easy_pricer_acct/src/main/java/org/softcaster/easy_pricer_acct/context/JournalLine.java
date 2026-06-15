/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import org.softcaster.engine.enums.NormalBalance;

/**
 *
 * @author ep
 */
public record JournalLine(String account, double amount, int currency, NormalBalance balance) {

}
