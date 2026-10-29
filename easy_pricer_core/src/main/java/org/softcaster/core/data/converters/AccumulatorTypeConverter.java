package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.AccumulatorType;

@Converter(autoApply = true)
public class AccumulatorTypeConverter extends AbstractIdentifiableEnumConverter<AccumulatorType> {

    public AccumulatorTypeConverter() {
        super(AccumulatorType.class);
    }
}
