package org.softcaster.core.data;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Bucket di una calibrazione passata (yield_curve_spread_hist): ci finisce la
 * calibrazione corrente quando ne arriva una piu' recente, o una calibrazione
 * con data precedente alla corrente. Serve alle valutazioni a date passate.
 */
@Entity
@Table(name = "yield_curve_spread_hist")
public class YieldCurveSpreadHist extends AbstractYieldCurveSpread {

    @Id
    @SequenceGenerator(name = "yield_curve_spread_hist_seq", sequenceName = "yield_curve_spread_hist_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yield_curve_spread_hist_seq")
    @Column(name = "id_yield_curve_spread_hist", columnDefinition = "INTEGER")
    private Integer idYieldCurveSpreadHist;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    protected YieldCurveSpreadHist() {
    }

    private YieldCurveSpreadHist(AbstractYieldCurveSpread s, LocalDate asOfDate) {
        super(s);
        this.asOfDate = asOfDate;
    }

    public static YieldCurveSpreadHist of(AbstractYieldCurveSpread s, LocalDate asOfDate) {
        return new YieldCurveSpreadHist(s, asOfDate);
    }

    public Integer getIdYieldCurveSpreadHist() {
        return idYieldCurveSpreadHist;
    }

    @Override
    public LocalDate getAsOfDate() {
        return asOfDate;
    }
}
