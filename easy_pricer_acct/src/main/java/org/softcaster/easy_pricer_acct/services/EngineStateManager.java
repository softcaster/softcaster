/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class EngineStateManager {

    // Di default il motore è attivo (false = non sospeso)
    private final AtomicBoolean suspended = new AtomicBoolean(false);

    public void suspend() {
        this.suspended.set(true);
    }

    public void resume() {
        this.suspended.set(false);
    }

    public boolean isSuspended() {
        return this.suspended.get();
    }
}
