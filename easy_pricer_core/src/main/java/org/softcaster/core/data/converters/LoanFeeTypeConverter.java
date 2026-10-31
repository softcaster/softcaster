package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.LoanFeeType;

@Converter(autoApply = true)
public class LoanFeeTypeConverter extends AbstractIdentifiableEnumConverter<LoanFeeType> {

    public LoanFeeTypeConverter() {
        super(LoanFeeType.class);
    }
}
