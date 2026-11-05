package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.EodStep;

@Converter(autoApply = true)
public class EodStepConverter extends AbstractIdentifiableEnumConverter<EodStep> {

    public EodStepConverter() {
        super(EodStep.class);
    }
}
