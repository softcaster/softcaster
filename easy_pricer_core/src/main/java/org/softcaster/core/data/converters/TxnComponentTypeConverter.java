/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.softcaster.core.data.enums.TxnComponentType;

@Converter(autoApply = true)
// autoApply = true significa che JPA applicherà questo convertitore 
// automaticamente ovunque troverà l'enum TxnComponentType
public class TxnComponentTypeConverter implements AttributeConverter<TxnComponentType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TxnComponentType attribute) {
        if (attribute == null) {
            return null;
        }
        // Associa ogni Enum all'ID numerico presente sul tuo Database
        return switch (attribute) {
            case BROKER_FEE ->
                1;
            case EXCHANGE_FEE ->
                2;
            case INITIAL_MARGIN ->
                3;
            case MAINTENANCE_MARGIN ->
                4;
            case OPTION_PREMIUM ->
                5;
        };
    }

    @Override
    public TxnComponentType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        // Traduce l'ID numerico letto da Postgres nel rispettivo Enum Java
        return switch (dbData) {
            case 1 ->
                TxnComponentType.BROKER_FEE;
            case 2 ->
                TxnComponentType.EXCHANGE_FEE;
            case 3 ->
                TxnComponentType.INITIAL_MARGIN;
            case 4 ->
                TxnComponentType.MAINTENANCE_MARGIN;
            case 5 ->
                TxnComponentType.OPTION_PREMIUM;
            default ->
                throw new IllegalArgumentException("Unknown database ID for TxnComponentType: " + dbData);
        };
    }
}
