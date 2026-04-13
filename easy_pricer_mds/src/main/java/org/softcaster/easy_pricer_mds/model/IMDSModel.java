/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_mds.model;

/**
 *
 * @author softc
 */
public interface IMDSModel {

    // Restituisce il valore per una colonna specifica
    public Object getValueAt(int columnIndex);

    // Restituisce i nomi delle colonne
    public String[] getColumnNames();
}
