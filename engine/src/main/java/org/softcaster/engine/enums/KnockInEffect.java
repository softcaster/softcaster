package org.softcaster.engine.enums;

/**
 * Effetto del knock-in di un accumulator: attiva la leva sui fixing
 * sfavorevoli (prima del knock-in valgono con leva 1) oppure l'intero
 * contratto (prima del knock-in non si accumula).
 */
public enum KnockInEffect implements IdentifiableEnum {
    LEVERAGE(1, "LEVERAGE", "Activates the leverage on unfavourable fixings"),
    CONTRACT(2, "CONTRACT", "Activates the contract");

    private final int id;
    private final String code;
    private final String description;

    KnockInEffect(int id, String code, String description) {
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

    public static KnockInEffect fromId(int id) {
        return IdentifiableEnum.fromId(KnockInEffect.class, id);
    }

    public static KnockInEffect fromCode(String code) {
        return IdentifiableEnum.fromCode(KnockInEffect.class, code);
    }
}
