/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.commons.utils;

/**
 *
 * @author ep
 */
// Obbliga gli enum ad averela stessa interfaccia
public interface IdentifiableEnum {

    int getId();

    String getCode();

    String getDescription();
}
