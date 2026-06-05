/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_shared.converters;

import jakarta.persistence.AttributeConverter;
import org.softcaster.easy_pricer_shared.enums.IdentifiableEnum;

/**
 * Convertitore JPA generico base per tutti gli enum che implementano
 * IdentifiableEnum.
 *
 * @param <E> Il tipo di Enum specifico
 */
public abstract class AbstractIdentifiableEnumConverter<E extends Enum<E> & IdentifiableEnum>
        implements AttributeConverter<E, Integer> {

    private final Class<E> enumClass;

    // Il costruttore richiede la classe dell'enum per permettere la riflessione a tempo di esecuzione
    protected AbstractIdentifiableEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        // Se l'oggetto Java è nullo, scrive NULL nella colonna INTEGER di PostgreSQL
        return (attribute == null) ? null : attribute.getId();
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        // Se la colonna del DB è NULL, restituisce null in Java, altrimenti usa il metodo generico dell'interfaccia
        return (dbData == null) ? null : IdentifiableEnum.fromId(enumClass, dbData);
    }
}
