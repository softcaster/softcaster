package org.softcaster.engine.enums;

/**
 * Stato di una chiusura di giornata (eod_run) e dei suoi step (eod_step_run).
 */
public enum EodStatus implements IdentifiableEnum {
    RUNNING(1, "RUNNING", "Running"),
    OK(2, "OK", "Completed"),
    WARNING(3, "WARNING", "Completed with warnings"),
    FAILED(4, "FAILED", "Failed");

    private final int id;
    private final String code;
    private final String description;

    EodStatus(int id, String code, String description) {
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

    public static EodStatus fromId(int id) {
        return IdentifiableEnum.fromId(EodStatus.class, id);
    }

    public static EodStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(EodStatus.class, code);
    }
}
