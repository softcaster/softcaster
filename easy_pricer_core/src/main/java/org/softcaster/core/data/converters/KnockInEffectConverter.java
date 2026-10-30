package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.KnockInEffect;

@Converter(autoApply = true)
public class KnockInEffectConverter extends AbstractIdentifiableEnumConverter<KnockInEffect> {

    public KnockInEffectConverter() {
        super(KnockInEffect.class);
    }
}
