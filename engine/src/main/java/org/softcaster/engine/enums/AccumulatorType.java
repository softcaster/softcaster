package org.softcaster.engine.enums;

/**
 * Verso di un accumulator: chi detiene il contratto compra (ACCUMULATOR) o
 * vende (DECUMULATOR) il sottostante allo strike a ogni fixing. Il knock-out
 * e' dalla parte favorevole (sopra lo strike per l'accumulator, sotto per il
 * decumulator), il knock-in dalla parte opposta.
 */
public enum AccumulatorType implements IdentifiableEnum {
    ACCUMULATOR(1, "ACCUMULATOR", "Accumulator (buys at strike, KO above)", 1),
    DECUMULATOR(2, "DECUMULATOR", "Decumulator (sells at strike, KO below)", -1);

    private final int id;
    private final String code;
    private final String description;
    private final int sign;

    AccumulatorType(int id, String code, String description, int sign) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.sign = sign;
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
     * +1 accumulator, -1 decumulator: il valore di un fixing per chi detiene il
     * contratto e' sign * (S - K) * quantita'.
     */
    public int getSign() {
        return sign;
    }

    /**
     * Fixing favorevole: sopra lo strike per l'accumulator, sotto per il
     * decumulator.
     */
    public boolean isFavourable(double spot, double strike) {
        return sign * (spot - strike) > 0.;
    }

    /**
     * Barriera di knock-out toccata.
     */
    public boolean isKnockedOut(double spot, double koBarrier) {
        return sign * (spot - koBarrier) >= 0.;
    }

    /**
     * Barriera di knock-in toccata.
     */
    public boolean isKnockedIn(double spot, double kiBarrier) {
        return sign * (spot - kiBarrier) <= 0.;
    }

    public static AccumulatorType fromId(int id) {
        return IdentifiableEnum.fromId(AccumulatorType.class, id);
    }

    public static AccumulatorType fromCode(String code) {
        return IdentifiableEnum.fromCode(AccumulatorType.class, code);
    }
}
