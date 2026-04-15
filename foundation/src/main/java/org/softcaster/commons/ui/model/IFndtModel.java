/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.commons.ui.model;

/**
 *
 * @author softc
 */
public interface IFndtModel {

    // Restituisce il valore per una colonna specifica
    public Object getValueAt(int columnIndex);

    // Restituisce i nomi delle colonne
    public String[] getColumnNames();
}
