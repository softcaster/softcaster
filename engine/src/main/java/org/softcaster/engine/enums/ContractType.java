package org.softcaster.engine.enums;

/**
 * Tipo di contratto quotato negoziabile: discriminatore di listed_contract,
 * tabella di lookup contract_type.
 *
 * Intervalli di id (usati dai CHECK e dagli indici di listed_contract):
 *   1-9   future
 *   11-19 opzioni
 *
 * JPA non applica converter al discriminatore: le sottoclassi di
 * ListedContract usano le costanti *_ID in @DiscriminatorValue. Il blocco
 * statico verifica all'avvio che costanti e id coincidano.
 */
public enum ContractType implements IdentifiableEnum {

    BOND_FUTURE(1, "BOND_FUTURE", "Bond future"),
    FX_FUTURE(2, "FX_FUTURE", "Currency future"),
    MM_FUTURE(3, "MM_FUTURE", "Money market future"),
    CMD_FUTURE(4, "CMD_FUTURE", "Commodity future"),
    OPTION(11, "OPTION", "Listed option");

    public static final String BOND_FUTURE_ID = "1";
    public static final String FX_FUTURE_ID = "2";
    public static final String MM_FUTURE_ID = "3";
    public static final String CMD_FUTURE_ID = "4";
    public static final String OPTION_ID = "11";

    static {
        check(BOND_FUTURE, BOND_FUTURE_ID);
        check(FX_FUTURE, FX_FUTURE_ID);
        check(MM_FUTURE, MM_FUTURE_ID);
        check(CMD_FUTURE, CMD_FUTURE_ID);
        check(OPTION, OPTION_ID);
    }

    private final int id;
    private final String code;
    private final String description;

    ContractType(int id, String code, String description) {
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

    public boolean isFuture() {
        return id >= 1 && id <= 9;
    }

    public boolean isOption() {
        return id >= 11 && id <= 19;
    }

    public static ContractType fromId(int id) {
        return IdentifiableEnum.fromId(ContractType.class, id);
    }

    public static ContractType fromCode(String code) {
        return IdentifiableEnum.fromCode(ContractType.class, code);
    }

    private static void check(ContractType type, String discriminator) {
        if (type.id != Integer.parseInt(discriminator)) {
            throw new ExceptionInInitializerError("ContractType." + type + ": id " + type.id
                    + " diverso dalla costante del discriminatore " + discriminator);
        }
    }
}
