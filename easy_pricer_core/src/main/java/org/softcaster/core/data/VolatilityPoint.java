package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;

/**
 * Punto della superficie corrente: volatility_point contiene solo l'ultima
 * rilevazione di ogni superficie, senza data; la data e' in testata
 * (volatility_surface.reference_date) e referenceDate la riporta in lettura
 * (VolatilitySurfaceDAO). Le rilevazioni precedenti stanno in
 * volatility_point_hist.
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

    // data della testata, non e' una colonna
    @Transient
    private LocalDate referenceDate;

    public VolatilityPoint() {
    }

    /**
     * Punto quotato per strike.
     */
    public VolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source) {
        this(volatilitySurface, referenceDate, expiryDate, strike, volatility, source, null, null);
    }

    /**
     * Punto quotato per tenor e delta (delta forward della call).
     */
    public VolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source, String tenor, Double delta) {
        super(volatilitySurface, expiryDate, strike, volatility, source, tenor, delta);
        this.referenceDate = referenceDate;
    }

    public Integer getIdVolatilityPoint() {
        return idVolatilityPoint;
    }

    @Override
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * Data della testata, riportata in lettura (VolatilitySurfaceDAO).
     */
    void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }
}
