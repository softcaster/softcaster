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
