/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author softc
 */
public enum SettlementType {
    PHYSICAL_DELIVERY,
    CASH_SETTLED;

    // Cache the values to avoid array cloning on every call
    private static final SettlementType[] ENUMS = SettlementType.values();

    public static SettlementType fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        }
        return ENUMS[ordinal];
    }
}
