package org.softcaster.engine.curve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;

/**
 * Curva OIS con scadenze in mesi e anni (OIS CHF su SARON, Cbonds): 15M e
 * 15Y, 1M e 1Y sono quotazioni diverse.
 */
class CurveBootstrapperOisTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 19);

    private static MarketQuote mm(String tenor, double ratePct) {
        return quote(tenor, ratePct, Compounding.SIMPLE, CurveNodeType.MONEY_MARKET);
    }

    private static MarketQuote ois(String tenor, double ratePct) {
        return quote(tenor, ratePct, Compounding.COMPOUNDED, CurveNodeType.OIS);
    }

    private static MarketQuote quote(String tenor, double ratePct, Compounding compounding, CurveNodeType type) {
        int step = Integer.parseInt(tenor.substring(0, tenor.length() - 1));
        OffsetType unit = switch (tenor.charAt(tenor.length() - 1)) {
            case 'D' -> OffsetType.DAYS;
            case 'M' -> OffsetType.MONTHS;
            default -> OffsetType.YEARS;
        };
        return new MarketQuote(tenor, new Offset(step, unit), ratePct / 100., DaycountBasis.ACT_360, compounding, type);
    }

    /** OIS CHF del 19/11/2026 (senza il 40Y, uguale al 30Y). */
    private static List<MarketQuote> saron(boolean withStubs) {
        List<MarketQuote> q = new ArrayList<>(List.of(
                mm("7D", -0.04175), mm("1M", -0.02752), mm("3M", -0.0055), mm("6M", 0.0563), mm("9M", 0.14118),
                ois("1Y", 0.22), ois("2Y", 0.43013), ois("3Y", 0.52935), ois("5Y", 0.60983), ois("10Y", 0.7475),
                ois("12Y", 0.79283), ois("15Y", 0.8375), ois("20Y", 0.854), ois("30Y", 0.79983)));
        if (withStubs) {
            q.add(ois("15M", 0.2935));
            q.add(ois("18M", 0.349));
            q.add(ois("21M", 0.3945));
        }
        return q;
    }

    @Test
    void fifteenMonthsAndFifteenYearsAreDifferentQuotes() {
        List<CurveNode> nodes = CurveBootstrapper.bootstrap(TODAY, saron(true));
        int days15y = (int) ChronoUnit.DAYS.between(TODAY, TODAY.plusYears(15));
        CurveNode n15y = nodes.stream().filter(n -> n.days() == days15y).findFirst().orElse(null);
        assertNotNull(n15y);
        assertEquals("15Y", n15y.mq().symbol());
        // nessun nodo a 15, 18, 21 mesi: le scadenze non annuali non sono nodi
        for (int months : new int[]{15, 18, 21}) {
            int days = (int) ChronoUnit.DAYS.between(TODAY, TODAY.plusMonths(months));
            assertTrue(nodes.stream().noneMatch(n -> n.days() == days), months + "M must not be a node");
        }
        // un nodo all'anno fino a 30Y piu' i depositi sotto l'anno
        assertEquals(30 + 5, nodes.size());
    }

    @Test
    void stubTenorsBetweenQuotedYearsDoNotChangeTheCurve() {
        List<CurveNode> with = CurveBootstrapper.bootstrap(TODAY, saron(true));
        List<CurveNode> without = CurveBootstrapper.bootstrap(TODAY, saron(false));
        assertEquals(without.size(), with.size());
        for (int i = 0; i < with.size(); i++) {
            assertEquals(without.get(i).days(), with.get(i).days());
            assertEquals(without.get(i).df(), with.get(i).df(), 1e-15);
        }
    }

    @Test
    void negativeShortRatesGiveDiscountFactorsAboveOne() {
        List<CurveNode> nodes = CurveBootstrapper.bootstrap(TODAY, saron(true));
        assertTrue(nodes.get(0).df() > 1.0, "7D at -0.04% must discount above 1");
    }

    @Test
    void oneYearQuotedTwiceIsADuplicate() {
        List<MarketQuote> q = new ArrayList<>(saron(false));
        q.add(ois("12M", 0.22));
        assertThrows(IllegalArgumentException.class, () -> CurveBootstrapper.bootstrap(TODAY, q));
    }

    /** OIS USD del 19/11/2026 (Cbonds): depositi fino a 3M, OIS dal 3Y. */
    private static List<MarketQuote> sofrFromThreeYears() {
        return new ArrayList<>(List.of(
                mm("1M", 4.0297), mm("2M", 4.12384), mm("3M", 4.20056),
                ois("3Y", 4.608), ois("5Y", 4.575), ois("10Y", 4.649)));
    }

    @Test
    void yearsBeforeTheFirstOisAreInterpolatedFromTheLastDeposit() {
        List<CurveNode> nodes = CurveBootstrapper.bootstrap(TODAY, sofrFromThreeYears());
        // 1Y: OIS a un pagamento, par = 4,20056 + (12-3)/(36-3) x (4,608-4,20056) %
        double par1y = (4.20056 + 9. / 33. * (4.608 - 4.20056)) / 100.;
        int days1y = (int) ChronoUnit.DAYS.between(TODAY, TODAY.plusYears(1));
        CurveNode n1y = nodes.stream().filter(n -> n.days() == days1y).findFirst().orElseThrow();
        assertEquals(1. / (1. + par1y * days1y / 360.), n1y.df(), 1e-12);
        // piatto sul 3Y darebbe un fattore di sconto piu' basso
        assertTrue(n1y.df() > 1. / (1. + 0.04608 * days1y / 360.));
    }

    @Test
    void depositOnTheFirstYearIsNotDuplicated() {
        List<MarketQuote> q = sofrFromThreeYears();
        q.add(ois("1Y", 4.62));
        List<CurveNode> nodes = CurveBootstrapper.bootstrap(TODAY, q);
        int days1y = (int) ChronoUnit.DAYS.between(TODAY, TODAY.plusYears(1));
        assertEquals("1Y", nodes.stream().filter(n -> n.days() == days1y).findFirst().orElseThrow().mq().symbol());
    }

    @Test
    void oisTenorInDaysIsRejected() {
        List<MarketQuote> q = new ArrayList<>(saron(false));
        q.add(ois("7D", 0.1));
        assertThrows(IllegalArgumentException.class, () -> CurveBootstrapper.bootstrap(TODAY, q));
    }
}
