package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.BondValuation;

@Converter(autoApply = true)
public class BondValuationConverter extends AbstractIdentifiableEnumConverter<BondValuation> {

    public BondValuationConverter() {
        super(BondValuation.class);
    }
}
