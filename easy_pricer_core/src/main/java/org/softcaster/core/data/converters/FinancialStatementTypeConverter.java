/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.FinancialStatementType;

@Converter(autoApply = true)
public class FinancialStatementTypeConverter extends AbstractIdentifiableEnumConverter<FinancialStatementType> {
    public FinancialStatementTypeConverter() {
        super(FinancialStatementType.class);
    }
}