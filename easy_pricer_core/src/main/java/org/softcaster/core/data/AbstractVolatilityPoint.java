package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Volatilita' implicita rilevata per una scadenza e uno strike (strike null =
 * at the money). Colonne comuni alla superficie corrente (VolatilityPoint,
 * senza data: la data e' in testata, volatility_surface.reference_date) e
 * allo storico (VolatilityPointHist, con la data).
 *
 * tenor e delta: punto quotato per delta (griglie CME per tenor), con
 * scadenza e strike ricavati all'import; delta forward della call (la put
 * 25D e' la call 0,75). Vuoti per i punti quotati per strike.
 */
@MappedSuperclass
public abstract class AbstractVolatilityPoint implements Serializable {

    @Column(name = "volatility_surface", nullable = false)
    private Integer volatilitySurface;

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

    protected AbstractVolatilityPoint(Integer volatilitySurface, LocalDate expiryDate,
            Double strike, Double volatility, String source, String tenor, Double delta) {
        if ((tenor == null) != (delta == null)) {
            throw new IllegalArgumentException("Tenor and delta must be given together");
        }
        this.volatilitySurface = volatilitySurface;
        this.expiryDate = expiryDate;
        this.strike = strike;
        this.volatility = volatility;
        this.source = source == null ? "" : source;
        this.tenor = tenor;
        this.delta = delta;
    }

    /**
     * Stessi valori di un altro punto (corrente o storico), data esclusa.
     */
    protected AbstractVolatilityPoint(AbstractVolatilityPoint p) {
        this(p.volatilitySurface, p.expiryDate, p.strike, p.volatility, p.source, p.tenor, p.delta);
    }

    /**
     * Data di rilevazione del punto.
     */
    public abstract LocalDate getReferenceDate();

    public Integer getVolatilitySurface() {
        return volatilitySurface;
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
