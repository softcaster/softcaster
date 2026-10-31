package org.softcaster.engine.enums;

/**
 * Garanzia dei primi periodi di un accumulator: nel periodo garantito il
 * knock-out non e' attivo. LEVERAGED (standard): la leva si applica come
 * sempre; UNLEVERAGED: ogni fixing matura la quantita' base, qualunque sia il
 * prezzo.
 */
public enum GuaranteeType implements IdentifiableEnum {
    NONE(1, "NONE", "No guarantee"),
    LEVERAGED(2, "LEVERAGED", "KO not active, leverage applies"),
    UNLEVERAGED(3, "UNLEVERAGED", "KO not active, base quantity on every fixing");

    private final int id;
    private final String code;
    private final String description;

    GuaranteeType(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static GuaranteeType fromId(int id) {
        return IdentifiableEnum.fromId(GuaranteeType.class, id);
    }

    public static GuaranteeType fromCode(String code) {
        return IdentifiableEnum.fromCode(GuaranteeType.class, code);
    }
}
