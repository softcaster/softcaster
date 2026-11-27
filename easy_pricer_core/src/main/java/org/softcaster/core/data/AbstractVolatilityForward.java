package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Forward del sottostante a una scadenza, rilevato con la superficie di
 * volatilita' (stessa data di rilevazione): per le superfici su indice e' il
 * forward di parita' put-call delle opzioni di borsa, dividendi compresi.
 * spot = livello del sottostante all'import, se disponibile: serve a riportare
 * il forward a un livello diverso (F x spot corrente / spot).
 *
 * Colonne comuni ai forward correnti (VolatilityForward, senza data: la data
 * e' in testata, volatility_surface.reference_date) e allo storico
 * (VolatilityForwardHist, con la data).
 */
@MappedSuperclass
public abstract class AbstractVolatilityForward implements Serializable {

    @Column(name = "volatility_surface", nullable = false)
    private Integer volatilitySurface;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "forward", nullable = false)
    private Double forward;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "spot")
    private Double spot;

    @Column(name = "source", nullable = false)
    private String source = "";

    protected AbstractVolatilityForward() {
    }

    protected AbstractVolatilityForward(Integer volatilitySurface, LocalDate expiryDate, Double forward, Double spot,
            String source) {
        this.volatilitySurface = volatilitySurface;
        this.expiryDate = expiryDate;
        this.forward = forward;
        this.spot = spot;
        this.source = source == null ? "" : source;
    }

    /**
     * Stessi valori di un altro forward (corrente o storico), data esclusa.
     */
    protected AbstractVolatilityForward(AbstractVolatilityForward f) {
        this(f.volatilitySurface, f.expiryDate, f.forward, f.spot, f.source);
    }

    /**
     * Data di rilevazione del forward.
     */
    public abstract LocalDate getReferenceDate();

    public Integer getVolatilitySurface() {
        return volatilitySurface;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Double getForward() {
        return forward;
    }

    public Double getSpot() {
        return spot;
    }

    public String getSource() {
        return source;
    }
}
