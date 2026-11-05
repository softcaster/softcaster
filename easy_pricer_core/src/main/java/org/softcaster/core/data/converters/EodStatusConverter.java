package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.EodStatus;

@Converter(autoApply = true)
public class EodStatusConverter extends AbstractIdentifiableEnumConverter<EodStatus> {

    public EodStatusConverter() {
        super(EodStatus.class);
    }
}
