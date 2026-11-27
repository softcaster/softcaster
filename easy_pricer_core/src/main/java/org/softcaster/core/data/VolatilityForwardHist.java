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
 * Forward di una rilevazione passata (volatility_forward_hist): ci finiscono
 * i forward correnti quando arriva una superficie piu' recente, o i forward
 * di una rilevazione con data precedente alla corrente.
 */
@Entity
@Table(name = "volatility_forward_hist")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilityForwardHist extends AbstractVolatilityForward {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "volatility_forward_hist_seq", sequenceName = "volatility_forward_hist_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_forward_hist_seq")
    @Column(name = "id_volatility_forward_hist")
    private Integer idVolatilityForwardHist;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    protected VolatilityForwardHist() {
    }

    private VolatilityForwardHist(AbstractVolatilityForward f, LocalDate referenceDate) {
        super(f);
        this.referenceDate = referenceDate;
    }

    public static VolatilityForwardHist of(AbstractVolatilityForward f, LocalDate referenceDate) {
        return new VolatilityForwardHist(f, referenceDate);
    }

    public Integer getIdVolatilityForwardHist() {
        return idVolatilityForwardHist;
    }

    @Override
    public LocalDate getReferenceDate() {
        return referenceDate;
    }
}
