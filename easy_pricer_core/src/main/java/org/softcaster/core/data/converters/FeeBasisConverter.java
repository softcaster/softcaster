package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.FeeBasis;

@Converter(autoApply = true)
public class FeeBasisConverter extends AbstractIdentifiableEnumConverter<FeeBasis> {

    public FeeBasisConverter() {
        super(FeeBasis.class);
    }
}
