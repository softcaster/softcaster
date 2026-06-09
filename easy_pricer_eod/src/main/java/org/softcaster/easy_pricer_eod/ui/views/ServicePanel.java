/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_eod.ui.views;

/**
 *
 * @author ep
 */
public interface ServicePanel {

    void startService();

    void stopService();

    void suspendService();

    void refreshStatus();

    String getServiceName(); // Deve corrispondere al testo del nodo dell'albero (es. "Rest Engine")

}
