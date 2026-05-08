/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.enums;

import java.util.Arrays;

public class EnumUtils {
    // Nota: il simbolo & viene usato nei Generics per definire un Multiple Bond (un vincolo multiplo
    // L'oggetto passato deve essere un Enum e deve implementare l'interfaccia IdentifiableEnum
    public static <T extends Enum<T> & IdentifiableEnum> T fromCode(Class<T> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equalsIgnoreCase(code) || e.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code for " + enumClass.getSimpleName() + ": " + code));
    }

    public static <T extends Enum<T> & IdentifiableEnum> T fromId(Class<T> enumClass, int id) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ID for " + enumClass.getSimpleName() + ": " + id));
    }
}
