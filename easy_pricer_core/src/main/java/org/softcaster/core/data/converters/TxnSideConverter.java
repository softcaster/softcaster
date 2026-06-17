/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.TxnSide;

@Converter(autoApply = true)
public class TxnSideConverter extends AbstractIdentifiableEnumConverter<TxnSide> {

    public TxnSideConverter() {
        super(TxnSide.class);
    }
}
