package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.FeeTiming;

@Converter(autoApply = true)
public class FeeTimingConverter extends AbstractIdentifiableEnumConverter<FeeTiming> {

    public FeeTimingConverter() {
        super(FeeTiming.class);
    }
}
