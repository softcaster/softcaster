package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.PositionType;

@Converter(autoApply = true)
public class PositionTypeConverter extends AbstractIdentifiableEnumConverter<PositionType> {

    public PositionTypeConverter() {
        super(PositionType.class);
    }
}
