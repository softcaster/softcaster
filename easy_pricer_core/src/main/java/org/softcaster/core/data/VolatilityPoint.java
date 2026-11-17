package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Punto della superficie corrente: volatility_point contiene solo l'ultima
 * rilevazione di ogni superficie, le precedenti stanno in
 * volatility_point_hist (VolatilitySurfaceDAO).
 */
@Entity
@Table(name = "volatility_point")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityPoint extends AbstractVolatilityPoint {

    @Id
    @SequenceGenerator(name = "volatility_point_seq", sequenceName = "volatility_point_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_point_seq")
    @Column(name = "id_volatility_point")
    private Integer idVolatilityPoint;

    public VolatilityPoint() {
    }

    /**
     * Punto quotato per strike.
     */
    public VolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source) {
        super(volatilitySurface, referenceDate, expiryDate, strike, volatility, source, null, null);
    }

    /**
     * Punto quotato per tenor e delta (delta forward della call).
     */
    public VolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source, String tenor, Double delta) {
        super(volatilitySurface, referenceDate, expiryDate, strike, volatility, source, tenor, delta);
    }

    public Integer getIdVolatilityPoint() {
        return idVolatilityPoint;
    }

    /**
     * La superficie corrente passa a una rilevazione piu' recente (VolatilitySurfaceDAO).
     */
    void moveTo(LocalDate referenceDate) {
        setReferenceDate(referenceDate);
    }
}
