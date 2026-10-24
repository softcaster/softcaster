package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.OptionType;

@Converter(autoApply = true)
public class OptionTypeConverter extends AbstractIdentifiableEnumConverter<OptionType> {

    public OptionTypeConverter() {
        super(OptionType.class);
    }
}