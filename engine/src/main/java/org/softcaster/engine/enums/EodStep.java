package org.softcaster.engine.enums;

/**
 * Step della chiusura di giornata, nell'ordine di esecuzione. I codici dei task
 * coincidono con EodTaskResult.task.
 */
public enum EodStep implements IdentifiableEnum {
    OPEN_CLOSING(1, "OPEN_CLOSING", "Set the next business date"),
    COUPON(2, "COUPON", "Coupon events"),
    SETTLEMENT(3, "SETTLEMENT", "Settlement events"),
    WAIT_SETTLEMENT_POSTED(4, "WAIT_SETTLEMENT_POSTED", "Wait for settlement accounting"),
    FX_ROLLING(5, "FX_ROLLING", "FX spot rolling"),
    ACCRUAL(6, "ACCRUAL", "Accrual events"),
    MTM(7, "MTM", "End of day mark to market"),
    WAIT_EOD_POSTED(8, "WAIT_EOD_POSTED", "Wait for end of day accounting"),
    CONTROLS(9, "CONTROLS", "Closing controls"),
    DATE_ROLL(10, "DATE_ROLL", "Roll the official date");

    private final int id;
    private final String code;
    private final String description;

    EodStep(int id, String code, String description) {
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

    public static EodStep fromId(int id) {
        return IdentifiableEnum.fromId(EodStep.class, id);
    }

    public static EodStep fromCode(String code) {
        return IdentifiableEnum.fromCode(EodStep.class, code);
    }
}
