/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.types.DateParser;

/**
 *
 * @author Emy
 */
public abstract class Converter {

    public static Locale locale = Locale.US;
    public static String patternDouble = "###,##0.00000";

    //
    // Conversione da string a valore
    //
    public static Boolean toBoolean(String value) throws ParseException {
        switch (value.toUpperCase()) {
            case "TRUE" -> {
                return true;
            }
            case "FALSE" -> {
                return false;
            }
            default -> throw new ParseException("Error converting [" + value + "] to boolean value", -1);
        }
    }

    public static Short toShort(String value) throws ParseException {
        if (value == null || value.length() <= 0) {
            return null;
        }

        Number number = null;
        short shortValue = 0;
        number = DecimalFormat.getIntegerInstance(locale).parse(value);
        if (number != null) {
            shortValue = number.shortValue();
        }
        return shortValue;
    }

    public static Integer toInt(String value) throws ParseException {
        if (value == null || value.length() <= 0) {
            return null;
        }

        int intValue = 0;
        Number number = DecimalFormat.getIntegerInstance(locale).parse(value);
        if (number != null) {
            intValue = number.intValue();
        }
        return intValue;
    }

    public static Double toDouble(String value, boolean rounded) throws ParseException {
        if (value == null || value.length() <= 0) {
            return null;
        }

        double doubleValue = 0.0;
        Number number = DecimalFormat.getNumberInstance(locale).parse(value);
        if (number != null) {
            doubleValue = number.doubleValue();
        }

        // Arrotondo
        if (rounded) {
            BigDecimal aNumber = org.softcaster.commons.utils.MoneyCalculation.rounded(doubleValue);
            doubleValue = aNumber.doubleValue();
        }
        return doubleValue;
    }

    //
    // Conversione da string a valore
    //
    public static String fromInt(Integer value) {
        return fromInt(value, null);
    }

    public static String fromInt(Integer value, String pattern) {
        if (pattern != null) {
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            return myFormatter.format(value);
        } else {
            return value.toString();
        }
    }

    public static String fromDouble(Double value) {
        return fromDouble(value, patternDouble);
    }

    public static String fromDouble(Double value, String pattern) {
        if (pattern != null) {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            DecimalFormat df = (DecimalFormat) nf;
            df.applyPattern(pattern);
            //DecimalFormat df = new DecimalFormat(pattern);
            return df.format(value.doubleValue());
        } else {
            return value.toString();
        }
    }

    public static String sqlDateToString(java.sql.Date sqlDate) {
        Date dt = new Date(sqlDate);
        return dt.toString();
    }

    public static Date stringToDate(String dtStr) {
        if (dtStr == null || dtStr.length() <= 0) {
            return null;
        }

        try {
            DateParser dp = new DateParser(dtStr);
            Date dt = new Date(dp.year(), dp.month(), dp.day());
            if (dt != null && dt.isValid()) {
                return dt;
            } else {
                return null;
            }
        } catch (Exception e) {
            return new Date(1, 1, 1);
        }
    }

    /**
     * @return the patternDouble
     */
    public static String getPatternDouble() {
        return patternDouble;
    }

    /**
     * @param aPatternDouble the patternDouble to set
     */
    public static void setPatternDouble(String aPatternDouble) {
        patternDouble = aPatternDouble;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.valueOf(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
