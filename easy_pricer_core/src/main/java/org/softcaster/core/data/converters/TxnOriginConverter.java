package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.TxnOrigin;

@Converter(autoApply = true)
public class TxnOriginConverter extends AbstractIdentifiableEnumConverter<TxnOrigin> {

    public TxnOriginConverter() {
        super(TxnOrigin.class);
    }
}
