package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Superficie di volatilita' di un sottostante (una per sottostante). I punti
 * (VolatilityPoint) sono per data di rilevazione e si leggono con
 * VolatilitySurfaceDAO, non come collezione: lo storico cresce ogni giorno.
 */
@Entity
@Table(name = "volatility_surface")
@SuppressWarnings("PersistenceUnitPresent")
public class VolatilitySurfaceEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "volatility_surface_seq", sequenceName = "volatility_surface_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "volatility_surface_seq")
    @Column(name = "id_volatility_surface")
    private Integer idVolatilitySurface;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description = "";

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private MasterData underlying;

    /**
     * Eta' massima della rilevazione, in giorni di calendario dalla data di
     * valutazione: oltre, la superficie non si usa (come gli z-spread).
     */
    @Column(name = "max_age_days", nullable = false)
    private Integer maxAgeDays = 5;

    /**
     * Data di rilevazione della superficie corrente (volatility_point e
     * volatility_forward): la scrive solo VolatilitySurfaceDAO, il salvataggio
     * della testata non la tocca.
     */
    @Column(name = "reference_date", insertable = false, updatable = false)
    private java.time.LocalDate referenceDate;

    /**
     * @return data della superficie corrente, null se senza punti
     */
    public java.time.LocalDate getReferenceDate() {
        return referenceDate;
    }

    public Integer getMaxAgeDays() {
        return maxAgeDays;
    }

    public void setMaxAgeDays(Integer maxAgeDays) {
        this.maxAgeDays = maxAgeDays;
    }

    public Integer getIdVolatilitySurface() {
        return idVolatilitySurface;
    }

    public void setIdVolatilitySurface(Integer idVolatilitySurface) {
        this.idVolatilitySurface = idVolatilitySurface;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
    }

    @Override
    public String toString() {
        return code;
    }
}
