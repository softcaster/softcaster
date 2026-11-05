package org.softcaster.engine.enums;

/**
 * Criterio di valutazione dei titoli, parametro della posizione
 * (position_master_data.bond_valuation).
 */
public enum BondValuation implements IdentifiableEnum {
    FAIR_VALUE(1, "FAIR_VALUE", "Fair value"),
    AMORTIZED_COST(2, "AMORTIZED_COST", "Amortized cost");

    private final int id;
    private final String code;
    private final String description;

    BondValuation(int id, String code, String description) {
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

    public static BondValuation fromId(int id) {
        return IdentifiableEnum.fromId(BondValuation.class, id);
    }

    public static BondValuation fromCode(String code) {
        return IdentifiableEnum.fromCode(BondValuation.class, code);
    }
}
