/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class DateParser {

    // Definiamo il parser con i vari pattern opzionali
    private static final DateTimeFormatter MULTI_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("ddMMyyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("ddMMyy"))
            .toFormatter();

    public static LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("String can't be empty");
        }

        try {
            return LocalDate.parse(dateStr, MULTI_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Formato data non supportato: " + dateStr, dateStr, 0);
        }
    }
}
