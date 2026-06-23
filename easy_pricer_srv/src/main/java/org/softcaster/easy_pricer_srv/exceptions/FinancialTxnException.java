/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.exceptions;

/**
 *
 * @author softc
 */
// Spring intercetta automaticamente RuntimeException per il rollback
public class FinancialTxnException extends RuntimeException {

    public FinancialTxnException(String message) {
        super(message);
    }

    public FinancialTxnException(String message, Throwable cause) {
        super(message, cause);
    }
}
