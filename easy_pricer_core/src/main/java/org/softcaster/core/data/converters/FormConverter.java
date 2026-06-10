/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.Form;

/**
 *
 * @author ep
 */
@Converter(autoApply = true)
public class FormConverter extends AbstractIdentifiableEnumConverter<Form> {
    
    public FormConverter() {
        super(Form.class);
    }
    
}
