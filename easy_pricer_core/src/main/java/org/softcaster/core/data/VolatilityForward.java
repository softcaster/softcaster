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
 * Forward corrente della superficie (volatility_forward, senza data): la data
 * e' quella della superficie corrente in testata
 * (volatility_surface.reference_date), riportata in lettura da
 * VolatilitySurfaceDAO. Le rilevazioni precedenti stanno in
 * volatility_forward_hist.
 */
@Entity
@Table(name = "volatility_forward")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityForward extends AbstractVolatilityForward {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "volatility_forward_seq", sequenceName = "volatility_forward_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_forward_seq")
    @Column(name = "id_volatility_forward")
    private Integer idVolatilityForward;

    // data della testata, non e' una colonna
    @Transient
    private LocalDate referenceDate;

    public VolatilityForward() {
    }

    public VolatilityForward(Integer volatilitySurface, LocalDate referenceDate, LocalDate expiryDate, Double forward,
            Double spot, String source) {
        super(volatilitySurface, expiryDate, forward, spot, source);
        this.referenceDate = referenceDate;
    }

    public Integer getIdVolatilityForward() {
        return idVolatilityForward;
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
