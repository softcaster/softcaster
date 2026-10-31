package org.softcaster.engine.enums;

/**
 * Come si esprime una spesa: importo nella valuta del contratto, oppure
 * percentuale in punti (0.25 = 0,25%) di una base (capitale per le spese una
 * tantum, debito residuo a inizio periodo per quelle per rata).
 */
public enum FeeBasis implements IdentifiableEnum {
    AMOUNT(1, "AMOUNT", "Amount"),
    PERCENT(2, "PERCENT", "% (points)");

    private final int id;
    private final String code;
    private final String description;

    FeeBasis(int id, String code, String description) {
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

    /**
     * Importo della spesa dato il valore e la base (capitale o residuo).
     */
    public double amount(double value, double base) {
        return this == AMOUNT ? value : value / 100. * base;
    }

    public static FeeBasis fromId(int id) {
        return IdentifiableEnum.fromId(FeeBasis.class, id);
    }

    public static FeeBasis fromCode(String code) {
        return IdentifiableEnum.fromCode(FeeBasis.class, code);
    }
}
