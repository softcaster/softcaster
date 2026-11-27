package org.softcaster.core.data;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Bucket della calibrazione corrente di una spread curve: yield_curve_spread
 * contiene solo l'ultima calibrazione di ogni curva, senza data; la data e' in
 * testata (spread_curve.as_of_date) e asOfDate la riporta in lettura
 * (YieldCurveSpreadDAO). Le calibrazioni precedenti stanno in
 * yield_curve_spread_hist.
 */
@Entity
@Table(name = "yield_curve_spread")
public class YieldCurveSpread extends AbstractYieldCurveSpread {

    @Id
    @SequenceGenerator(name = "yield_curve_spread_seq", sequenceName = "yield_curve_spread_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "yield_curve_spread_seq")
    @Column(name = "id_yield_curve_spread", columnDefinition = "INTEGER")
    private Integer idYieldCurveSpread;

    // data della testata, non e' una colonna
    @Transient
    private LocalDate asOfDate;

    public Integer getIdYieldCurveSpread() {
        return idYieldCurveSpread;
    }

    public void setIdYieldCurveSpread(Integer idYieldCurveSpread) {
        this.idYieldCurveSpread = idYieldCurveSpread;
    }

    @Override
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }
}
