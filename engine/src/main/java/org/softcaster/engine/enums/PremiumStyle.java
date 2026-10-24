package org.softcaster.engine.enums;

/**
 * Modalita' di pagamento del premio di un'opzione.
 *
 * EQUITY_STYLE: premio pagato all'acquisto (va scontato dalla scadenza).
 * FUTURES_STYLE: premio regolato con margini giornalieri come un future (non
 * si sconta).
 */
public enum PremiumStyle implements IdentifiableEnum {
    EQUITY_STYLE(1, "EQUITY_STYLE", "Premium paid upfront"),
    FUTURES_STYLE(2, "FUTURES_STYLE", "Premium margined daily like a future");

    private final int id;
    private final String code;
    private final String description;

    PremiumStyle(int id, String code, String description) {
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
     * true se il premio va scontato (ForwardOptionInput.premiumDiscounted).
     */
    public boolean isDiscounted() {
        return this == EQUITY_STYLE;
    }

    public static PremiumStyle fromId(int id) {
        return IdentifiableEnum.fromId(PremiumStyle.class, id);
    }

    public static PremiumStyle fromCode(String code) {
        return IdentifiableEnum.fromCode(PremiumStyle.class, code);
    }
}
