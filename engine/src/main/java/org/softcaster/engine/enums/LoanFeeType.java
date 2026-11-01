package org.softcaster.engine.enums;

/**
 * Spese di un mutuo che entrano nel costo per il cliente (TAEG). Le
 * condizioni di servizio (mora, penali) non sono spese: restano
 * sull'anagrafica.
 */
public enum LoanFeeType implements IdentifiableEnum {
    PROCESSING(1, "PROCESSING", "Processing fee"),
    UNDERWRITING(2, "UNDERWRITING", "Underwriting / appraisal fee"),
    TAX(3, "TAX", "Tax charges"),
    INCIDENTAL(4, "INCIDENTAL", "Incidental expenses"),
    MANAGEMENT(5, "MANAGEMENT", "Management / collection fee"),
    INSURANCE(6, "INSURANCE", "Insurance premium");

    private final int id;
    private final String code;
    private final String description;

    LoanFeeType(int id, String code, String description) {
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

    public static LoanFeeType fromId(int id) {
        return IdentifiableEnum.fromId(LoanFeeType.class, id);
    }

    public static LoanFeeType fromCode(String code) {
        return IdentifiableEnum.fromCode(LoanFeeType.class, code);
    }
}
