/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_shared.enums;

public interface IdentifiableEnum {

    int getId();

    String getCode();

    String getDescription();

    /**
     * Metodo generico statico per cercare un enum tramite il suo ID.
     *
     * @param <E> Il tipo dell'enum che implementa IdentifiableEnum
     * @param enumClass La classe dell'enum (es. TxnComponentType.class)
     * @param id L'ID numerico da cercare
     * @return L'istanza dell'enum corrispondente
     */
    static <E extends Enum<E> & IdentifiableEnum> E fromId(Class<E> enumClass, int id) {
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getId() == id) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + " ID: " + id);
    }

    /**
     * Metodo generico statico per cercare un enum tramite il suo Codice
     * Stringa.
     * @param <E>
     * @param enumClass
     * @param code
     * @return 
     */
    static <E extends Enum<E> & IdentifiableEnum> E fromCode(Class<E> enumClass, String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getCode().equalsIgnoreCase(code)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + " code: " + code);
    }
}
