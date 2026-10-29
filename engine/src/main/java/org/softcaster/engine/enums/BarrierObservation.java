package org.softcaster.engine.enums;

/**
 * Come si osserva una barriera: al fixing, in continuo, alle date di
 * regolamento.
 */
public enum BarrierObservation implements IdentifiableEnum {
    FIXING(1, "FIXING", "At each fixing"),
    CONTINUOUS(2, "CONTINUOUS", "Continuous"),
    SETTLEMENT(3, "SETTLEMENT", "At settlement dates");

    private final int id;
    private final String code;
    private final String description;

    BarrierObservation(int id, String code, String description) {
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

    public static BarrierObservation fromId(int id) {
        return IdentifiableEnum.fromId(BarrierObservation.class, id);
    }

    public static BarrierObservation fromCode(String code) {
        return IdentifiableEnum.fromCode(BarrierObservation.class, code);
    }
}
