/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.accounting.context;

import java.math.BigDecimal;

/**
 *
 * @author ep
 */
public record JournalLine(String account, BigDecimal amount) {
    
}
