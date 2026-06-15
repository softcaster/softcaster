/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.exceptions;

/**
 *
 * @author softc
 */
// Spring intercetta automaticamente RuntimeException per il rollback
public class AccountingException extends RuntimeException {

    public AccountingException(String message) {
        super(message);
    }

    public AccountingException(String message, Throwable cause) {
        super(message, cause);
    }
}
