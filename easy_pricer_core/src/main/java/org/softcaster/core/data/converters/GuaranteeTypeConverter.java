package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.GuaranteeType;

@Converter(autoApply = true)
public class GuaranteeTypeConverter extends AbstractIdentifiableEnumConverter<GuaranteeType> {

    public GuaranteeTypeConverter() {
        super(GuaranteeType.class);
    }
}
