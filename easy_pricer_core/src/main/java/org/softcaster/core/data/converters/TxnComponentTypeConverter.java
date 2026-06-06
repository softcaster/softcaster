/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.TxnComponentType;

// autoApply = true significa che JPA applicherà questo convertitore 
// automaticamente ovunque troverà l'enum TxnComponentType

@Converter(autoApply = true)
public class TxnComponentTypeConverter extends AbstractIdentifiableEnumConverter<TxnComponentType> {

    // Al costruttore base passiamo semplicemente la classe dell'Enum per inizializzare il Generic
    public TxnComponentTypeConverter() {
        super(TxnComponentType.class);
    }
}