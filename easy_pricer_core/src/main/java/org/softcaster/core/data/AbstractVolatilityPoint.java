package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Volatilita' implicita rilevata a una data per una scadenza e uno strike
 * (strike null = at the money). Colonne comuni alla superficie corrente
 * (VolatilityPoint) e allo storico (VolatilityPointHist).
 *
 * tenor e delta: punto quotato per delta (griglie CME per tenor), con
 * scadenza e strike ricavati all'import; delta forward della call (la put
 * 25D e' la call 0,75). Vuoti per i punti quotati per strike.
 */
@MappedSuperclass
public abstract class AbstractVolatilityPoint implements Serializable {

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

    @Column(name = "tenor")
    private String tenor;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "delta")
    private Double delta;

    protected AbstractVolatilityPoint() {
    }

    protected AbstractVolatilityPoint(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate,
            Double strike, Double volatility, String source, String tenor, Double delta) {
        if ((tenor == null) != (delta == null)) {
            throw new IllegalArgumentException("Tenor and delta must be given together");
        }
        this.volatilitySurface = volatilitySurface;
        this.referenceDate = referenceDate;
        this.expiryDate = expiryDate;
        this.strike = strike;
        this.volatility = volatility;
        this.source = source == null ? "" : source;
        this.tenor = tenor;
        this.delta = delta;
    }

    /**
     * Stessi valori di un altro punto (corrente o storico).
     */
    protected AbstractVolatilityPoint(AbstractVolatilityPoint p) {
        this(p.volatilitySurface, p.referenceDate, p.expiryDate, p.strike, p.volatility, p.source, p.tenor, p.delta);
    }

    public Integer getVolatilitySurface() {
        return volatilitySurface;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    protected void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
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

    public String getTenor() {
        return tenor;
    }

    public Double getDelta() {
        return delta;
    }
}
