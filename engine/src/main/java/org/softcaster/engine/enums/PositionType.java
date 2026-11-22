package org.softcaster.engine.enums;

/**
 * Tipo di nodo della gerarchia delle posizioni (position_master_data.position_type).
 * Non e' long/short: indica il livello del nodo nell'albero. L'id fa da rango:
 * un figlio ha rango maggiore del padre, tranne GROUP che puo' stare sotto
 * qualunque nodo non postable. Solo POSITION e' postable e alimenta position_detail.
 */
public enum PositionType implements IdentifiableEnum {
    LEGAL_ENTITY(1, "LEGAL_ENTITY", "Legal entity"),
    BUSINESS_UNIT(2, "BUSINESS_UNIT", "Business unit"),
    DESK(3, "DESK", "Desk"),
    PORTFOLIO(4, "PORTFOLIO", "Portfolio"),
    GROUP(5, "GROUP", "Group"),
    POSITION(6, "POSITION", "Position");

    private final int id;
    private final String code;
    private final String description;

    PositionType(int id, String code, String description) {
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

    public boolean isPostable() {
        return this == POSITION;
    }

    /**
     * Regola di rango. rankingAncestorType e' il tipo del primo antenato che non
     * e' GROUP (null se non ce n'e'): i GROUP sono trasparenti, quindi sotto un
     * GROUP valgono i vincoli del nodo che lo contiene.
     */
    public boolean canBeChildOf(PositionType rankingAncestorType) {
        if (this == GROUP || rankingAncestorType == null) {
            return true;
        }
        return id > rankingAncestorType.id;
    }

    public static PositionType fromId(int id) {
        return IdentifiableEnum.fromId(PositionType.class, id);
    }

    public static PositionType fromCode(String code) {
        return IdentifiableEnum.fromCode(PositionType.class, code);
    }
}
