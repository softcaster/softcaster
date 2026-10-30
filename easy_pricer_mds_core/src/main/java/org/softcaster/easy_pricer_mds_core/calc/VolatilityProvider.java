package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.softcaster.core.data.VolatilityPoint;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.engine.volatility.GridVolatilitySurface;
import org.softcaster.engine.volatility.VolatilityNode;
import org.softcaster.engine.volatility.VolatilitySurface;

/**
 * Superficie di volatilita' di un sottostante a una data di valutazione:
 * punti dell'ultima rilevazione non successiva alla data, tempo misurato
 * dalla data di valutazione (scadenze gia' passate scartate).
 *
 * Creato come @Bean in MdsCoreAutoConfiguration.
 */
public class VolatilityProvider {

    /**
     * Superficie con la sua provenienza (codice e data di rilevazione).
     */
    public record SurfaceData(String code, LocalDate referenceDate, VolatilitySurface surface) {

        public String describe() {
            return code + " del " + referenceDate;
        }
    }

    private final VolatilitySurfaceDAO volatilitySurfaceDAO;

    public VolatilityProvider(VolatilitySurfaceDAO volatilitySurfaceDAO) {
        this.volatilitySurfaceDAO = Objects.requireNonNull(volatilitySurfaceDAO);
    }

    /**
     * Vuoto se il sottostante non ha superficie o non ci sono punti utili alla
     * data.
     */
    public Optional<SurfaceData> surface(Integer underlyingId, LocalDate valuationDate) {
        Optional<VolatilitySurfaceEntity> entity = volatilitySurfaceDAO.findByUnderlying(underlyingId);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        List<VolatilityPoint> points = volatilitySurfaceDAO.findLatestPoints(entity.get().getIdVolatilitySurface(), valuationDate);
        List<VolatilityNode> nodes = points.stream()
                .filter(p -> p.getExpiryDate().isAfter(valuationDate))
                .map(p -> new VolatilityNode(p.getExpiryDate(), p.getStrike(), p.getVolatility()))
                .toList();
        if (nodes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SurfaceData(entity.get().getCode(), points.get(0).getReferenceDate(),
                new GridVolatilitySurface(valuationDate, nodes)));
    }
}
