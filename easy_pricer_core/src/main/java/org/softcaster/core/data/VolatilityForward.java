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
 * Forward del sottostante a una scadenza, rilevato con la superficie di
 * volatilita' (stessa data di rilevazione): per le superfici su indice e' il
 * forward di parita' put-call delle opzioni di borsa, dividendi compresi.
 * spot = livello del sottostante all'import, se disponibile: serve a riportare
 * il forward a un livello diverso (F x spot corrente / spot).
 */
@Entity
@Table(name = "volatility_forward")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityForward implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "volatility_forward_seq", sequenceName = "volatility_forward_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_forward_seq")
    @Column(name = "id_volatility_forward")
    private Integer idVolatilityForward;

    @Column(name = "volatility_surface", nullable = false)
    private Integer volatilitySurface;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

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

    public VolatilityForward() {
    }

    public VolatilityForward(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate, Double forward,
            Double spot, String source) {
        this.volatilitySurface = volatilitySurface;
        this.referenceDate = referenceDate;
        this.expiryDate = expiryDate;
        this.forward = forward;
        this.spot = spot;
        this.source = source == null ? "" : source;
    }

    public Integer getIdVolatilityForward() {
        return idVolatilityForward;
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
