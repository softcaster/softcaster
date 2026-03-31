package ph.alephzero.finance;

public enum Compounding {
    SIMPLE,                    // (1 + rt)
    COMPOUNDED,                // (1 + r)^t
    SIMPLE_THEN_COMPOUNDED,    // SIMPLE if t <= 1 year, else COMPOUNDED
    CONTINUOUS;                // exp(rt)

    // Cache the values to avoid array cloning on every call
    private static final Compounding[] ENUMS = Compounding.values();

    public static Compounding fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        }
        return ENUMS[ordinal];
    }

    public static int toOrdinal(Compounding compounding) {
        return compounding.ordinal();
    }
}
