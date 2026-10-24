package org.softcaster.core.data.converters;

import jakarta.persistence.Converter;
import org.softcaster.engine.enums.ExerciseStyle;

@Converter(autoApply = true)
public class ExerciseStyleConverter extends AbstractIdentifiableEnumConverter<ExerciseStyle> {

    public ExerciseStyleConverter() {
        super(ExerciseStyle.class);
    }
}