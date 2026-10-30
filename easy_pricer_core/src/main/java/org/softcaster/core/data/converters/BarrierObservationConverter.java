package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.BarrierObservation;

@Converter(autoApply = true)
public class BarrierObservationConverter extends AbstractIdentifiableEnumConverter<BarrierObservation> {

    public BarrierObservationConverter() {
        super(BarrierObservation.class);
    }
}
