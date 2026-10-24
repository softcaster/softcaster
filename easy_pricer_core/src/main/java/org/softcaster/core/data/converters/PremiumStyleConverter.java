package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.PremiumStyle;

@Converter(autoApply = true)
public class PremiumStyleConverter extends AbstractIdentifiableEnumConverter<PremiumStyle> {

    public PremiumStyleConverter() {
        super(PremiumStyle.class);
    }
}