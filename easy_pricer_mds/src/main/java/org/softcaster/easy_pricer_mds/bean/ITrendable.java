/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

/**
 *
 * @author softc
 */
public interface ITrendable {
/**
     * 
     * @param columnIndex
     * @return > 0 per trend positivo, < 0 per negativo, 0 per stabile
     */
    int getTrendForColumn(int columnIndex);    
}
