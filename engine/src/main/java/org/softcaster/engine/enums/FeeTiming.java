package org.softcaster.engine.enums;

/**
 * Quando si paga una spesa: una volta all'erogazione o a ogni rata.
 */
public enum FeeTiming implements IdentifiableEnum {
    UPFRONT(1, "UPFRONT", "Once, at disbursement"),
    PER_INSTALLMENT(2, "PER_INSTALLMENT", "At every installment");

    private final int id;
    private final String code;
    private final String description;

    FeeTiming(int id, String code, String description) {
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

    public static FeeTiming fromId(int id) {
        return IdentifiableEnum.fromId(FeeTiming.class, id);
    }

    public static FeeTiming fromCode(String code) {
        return IdentifiableEnum.fromCode(FeeTiming.class, code);
    }
}
