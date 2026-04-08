/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.dialogs;

import java.text.ParseException;
import java.util.List;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.types.DateParser;
import org.softcaster.commons.utils.Converter;

/**
 *
 * @author ep
 */
public class MDDialogHelper {
    
    public static void textFieldDoubleFocusLost(javax.swing.JTextField textField) {
        String doubleStr = textField.getText();
        try {
            double price = Converter.toDouble(doubleStr, true);
            textField.setText(Converter.fromDouble(price));
        } catch (ParseException ex) {
            textField.setText("");
        }
    }

    public static void textFieldDateFocusLost(javax.swing.JTextField textField) {
        try {
            String dateStr = textField.getText();
            DateParser parser = new DateParser(dateStr);
            Date dt = new Date(parser.year(), parser.month(), parser.day());
            textField.setText(dt.toString());
        } catch (Exception ex) {
            textField.setText("");
        }
    }

    public static void textFieldDateKeyPressed(java.awt.event.KeyEvent evt, javax.swing.JTextField textField) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2) {
            textField.setText("");
            Date today = new Date();
            textField.setText(today.toString());
        }
    }
    
    /**
     *
     * @param fields
     * @return
     */
    public static boolean validateFields (List<javax.swing.JTextField> fields) {
        for(javax.swing.JTextField field: fields) {
            if(field.getText().isBlank())
                return false;
        }
        return true;
    }
}
