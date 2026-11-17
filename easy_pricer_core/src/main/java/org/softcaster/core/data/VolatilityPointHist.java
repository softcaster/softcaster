package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Punto di una rilevazione passata (volatility_point_hist): ci finisce la
 * superficie corrente quando ne arriva una piu' recente, o una rilevazione
 * caricata con data precedente alla corrente. Serve alle valutazioni a date
 * passate.
 */
@Entity
@Table(name = "volatility_point_hist")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityPointHist extends AbstractVolatilityPoint {

    @Id
    @SequenceGenerator(name = "volatility_point_hist_seq", sequenceName = "volatility_point_hist_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_point_hist_seq")
    @Column(name = "id_volatility_point_hist")
    private Integer idVolatilityPointHist;

    protected VolatilityPointHist() {
    }

    private VolatilityPointHist(AbstractVolatilityPoint p) {
        super(p);
    }

    public static VolatilityPointHist of(AbstractVolatilityPoint p) {
        return new VolatilityPointHist(p);
    }

    public Integer getIdVolatilityPointHist() {
        return idVolatilityPointHist;
    }
}
