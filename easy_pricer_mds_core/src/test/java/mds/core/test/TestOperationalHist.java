package mds.core.test;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.AbstractVolatilityForward;
import org.softcaster.core.data.AbstractVolatilityPoint;
import org.softcaster.core.data.AbstractYieldCurveSpread;
import org.softcaster.core.data.VolatilityForward;
import org.softcaster.core.data.VolatilityPoint;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.core.data.YieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Tabelle operative senza data (yield_curve_spread, volatility_point,
 * volatility_forward): letture a date diverse e scritture con data piu'
 * recente, uguale e precedente alla corrente. Scrive: da lanciare solo su una
 * copia del database.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.softcaster.core.data"})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestOperationalHist implements CommandLineRunner {

    private static final LocalDate D25 = LocalDate.of(2026, 11, 25);
    private static final LocalDate D26 = LocalDate.of(2026, 11, 26);

    @Autowired
    private YieldCurveSpreadDAO spreadDAO;
    @Autowired
    private VolatilitySurfaceDAO volDAO;

    public static void main(String[] args) {
        SpringApplication.run(TestOperationalHist.class, args);
    }

    @Override
    public void run(String... args) {
        spreads();
        index();
        fx();
        System.exit(0);
    }

    private void spreads() {
        printSpreads("current", spreadDAO.findLatest(1, D25));
        printSpreads("at 2026-11-20", spreadDAO.findLatest(1, LocalDate.of(2026, 11, 20)));
        printSpreads("at 2026-10-01", spreadDAO.findLatest(1, LocalDate.of(2026, 10, 1)));

        List<YieldCurveSpread> rows = spreadDAO.findLatest(1, D25).stream().map(r -> copy(r, 0.0001)).toList();
        spreadDAO.replaceCalibration(1, D26, rows);
        printSpreads("after 26 at 26", spreadDAO.findLatest(1, D26));
        printSpreads("after 26 at 25", spreadDAO.findLatest(1, D25));

        spreadDAO.replaceCalibration(1, D26, spreadDAO.findLatest(1, D25).stream().map(r -> copy(r, 0.0002)).toList());
        printSpreads("same date again at 26", spreadDAO.findLatest(1, D26));

        LocalDate d24 = LocalDate.of(2026, 11, 24);
        spreadDAO.replaceCalibration(1, d24, spreadDAO.findLatest(1, D25).stream().map(r -> copy(r, -0.0001)).toList());
        printSpreads("older 24 at 24", spreadDAO.findLatest(1, d24));
        printSpreads("older 24, current still", spreadDAO.findLatest(1, LocalDate.of(9999, 1, 1)));
    }

    private void index() {
        VolatilitySurfaceEntity s = volDAO.findByCode("SX5E-VOL").orElseThrow();
        Integer id = s.getIdVolatilitySurface();
        System.out.println("VOL header SX5E-VOL as of " + s.getReferenceDate());
        List<? extends AbstractVolatilityPoint> points = volDAO.findLatestPoints(id, D25);
        List<? extends AbstractVolatilityForward> forwards = volDAO.findLatestForwards(id, D25);
        printVol("SX5E current", points, forwards);

        List<VolatilityPoint> next = points.stream().filter(p -> p.getExpiryDate().isAfter(D26))
                .map(p -> new VolatilityPoint(id, D26, p.getExpiryDate(), p.getStrike(), p.getVolatility() + 0.01, p.getSource()))
                .toList();
        List<VolatilityForward> nextFwd = forwards.stream().filter(f -> f.getExpiryDate().isAfter(D26))
                .map(f -> new VolatilityForward(id, D26, f.getExpiryDate(), f.getForward() + 10, f.getSpot(), f.getSource()))
                .toList();
        try {
            volDAO.replaceForwards(id, D26, nextFwd);
            System.out.println("VOL ERROR forwards accepted before points");
        } catch (IllegalStateException e) {
            System.out.println("VOL forwards before points refused: " + e.getMessage());
        }
        volDAO.replacePoints(id, D26, next);
        printVol("SX5E after points 26 (forwards archived)", volDAO.findLatestPoints(id, D26), volDAO.findLatestForwards(id, D26));
        volDAO.replaceForwards(id, D26, nextFwd);
        printVol("SX5E 26 at 26", volDAO.findLatestPoints(id, D26), volDAO.findLatestForwards(id, D26));
        printVol("SX5E 26 at 25", volDAO.findLatestPoints(id, D25), volDAO.findLatestForwards(id, D25));
        System.out.println("VOL last date at 25 " + volDAO.findLastReferenceDate(id, D25).orElse(null)
                + ", at 26 " + volDAO.findLastReferenceDate(id, D26).orElse(null)
                + ", header " + volDAO.findByCode("SX5E-VOL").orElseThrow().getReferenceDate());
    }

    private void fx() {
        VolatilitySurfaceEntity s = volDAO.findByCode("EURUSD-VOL").orElseThrow();
        Integer id = s.getIdVolatilitySurface();
        List<? extends AbstractVolatilityPoint> points = volDAO.findLatestPoints(id, D25);
        printVol("EURUSD current", points, List.of());
        List<VolatilityPoint> next = points.stream().filter(p -> p.getTenor() != null && p.getExpiryDate().isAfter(D26))
                .limit(5)
                .map(p -> new VolatilityPoint(id, D26, p.getExpiryDate(), p.getStrike(), p.getVolatility() + 0.01, p.getSource(),
                        p.getTenor(), p.getDelta()))
                .toList();
        if (next.isEmpty()) {
            System.out.println("VOL EURUSD has no tenor points, skipped");
            return;
        }
        volDAO.updateTenorPoints(id, D26, next);
        printVol("EURUSD tenor update 26 at 26", volDAO.findLatestPoints(id, D26), List.of());
        printVol("EURUSD tenor update 26 at 25", volDAO.findLatestPoints(id, D25), List.of());
    }

    private static YieldCurveSpread copy(AbstractYieldCurveSpread r, double bump) {
        YieldCurveSpread c = new YieldCurveSpread();
        c.setSpreadCurveId(r.getSpreadCurveId());
        c.setBaseCurveCode(r.getBaseCurveCode());
        c.setOffsetType(r.getOffsetType());
        c.setOffsetValue(r.getOffsetValue());
        c.setzSpread(r.getzSpread() + bump);
        c.setnBonds(r.getnBonds());
        return c;
    }

    private static void printSpreads(String label, List<? extends AbstractYieldCurveSpread> rows) {
        System.out.println("ZSP " + label + ": " + rows.size() + " rows "
                + (rows.isEmpty() ? "" : rows.get(0).getClass().getSimpleName() + " as of " + rows.get(0).getAsOfDate()
                + " first " + rows.get(0).getOffsetValue() + "Y " + rows.get(0).getzSpread()));
    }

    private static void printVol(String label, List<? extends AbstractVolatilityPoint> points,
            List<? extends AbstractVolatilityForward> forwards) {
        System.out.println("VOL " + label + ": " + points.size() + " points "
                + (points.isEmpty() ? "" : points.get(0).getClass().getSimpleName() + " as of " + points.get(0).getReferenceDate()
                + " first vol " + points.get(0).getVolatility())
                + ", " + forwards.size() + " forwards "
                + (forwards.isEmpty() ? "" : forwards.get(0).getClass().getSimpleName() + " as of " + forwards.get(0).getReferenceDate()
                + " first " + forwards.get(0).getForward()));
    }
}
