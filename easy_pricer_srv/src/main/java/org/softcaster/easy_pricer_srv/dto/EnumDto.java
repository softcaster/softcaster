package org.softcaster.easy_pricer_srv.dto;

import org.softcaster.engine.enums.IdentifiableEnum;

/**
 * Voce di un enum per i client: code e' il valore da rimandare nelle
 * richieste, description l'etichetta da mostrare.
 */
public record EnumDto(int id, String code, String description) {

    public static EnumDto of(IdentifiableEnum e) {
        return new EnumDto(e.getId(), e.getCode(), e.getDescription());
    }
}
