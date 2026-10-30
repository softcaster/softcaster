package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Volatilita' implicita rilevata a una data per una scadenza e uno strike
 * (strike null = at the money).
 */
@Entity
@Table(name = "volatility_point")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityPoint implements Serializable {

    @Id
    @SequenceGenerator(name = "volatility_point_seq", sequenceName = "volatility_point_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_point_seq")
    @Column(name = "id_volatility_point")
    private Integer idVolatilityPoint;

    @Column(name = "volatility_surface", nullable = false)
    private Integer volatilitySurface;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "strike")
    private Double strike;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "volatility", nullable = false)
    private Double volatility;

    @Column(name = "source", nullable = false)
    private String source = "";

    public VolatilityPoint() {
    }

    public VolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source) {
        this.volatilitySurface = volatilitySurface;
        this.referenceDate = referenceDate;
        this.expiryDate = expiryDate;
        this.strike = strike;
        this.volatility = volatility;
        this.source = source == null ? "" : source;
    }

    public Integer getIdVolatilityPoint() {
        return idVolatilityPoint;
    }

    public Integer getVolatilitySurface() {
        return volatilitySurface;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Double getStrike() {
        return strike;
    }

    public Double getVolatility() {
        return volatility;
    }

    public String getSource() {
        return source;
    }
}
