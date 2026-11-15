package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
            return code + " as of " + referenceDate;
        }
    }

    private final VolatilitySurfaceDAO volatilitySurfaceDAO;

    public VolatilityProvider(VolatilitySurfaceDAO volatilitySurfaceDAO) {
        this.volatilitySurfaceDAO = Objects.requireNonNull(volatilitySurfaceDAO);
    }

    /**
     * Vuoto se il sottostante non ha superficie o non ci sono punti utili alla
     * data.
     *
     * @throws IllegalStateException se l'ultima rilevazione e' piu' vecchia di
     * max_age_days: valutare con volatilita' vecchie darebbe valori sbagliati
     * senza avvisare (stessa regola degli z-spread)
     */
    public Optional<SurfaceData> surface(Integer underlyingId, LocalDate valuationDate) {
        Optional<VolatilitySurfaceEntity> entity = volatilitySurfaceDAO.findByUnderlying(underlyingId);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        List<VolatilityPoint> points = volatilitySurfaceDAO.findLatestPoints(entity.get().getIdVolatilitySurface(), valuationDate);
        if (!points.isEmpty()) {
            LocalDate asOf = points.get(0).getReferenceDate();
            long ageDays = ChronoUnit.DAYS.between(asOf, valuationDate);
            Integer maxAge = entity.get().getMaxAgeDays();
            if (maxAge != null && ageDays > maxAge) {
                throw new IllegalStateException("Volatility surface " + entity.get().getCode() + " is stale: as of " + asOf
                        + " (" + ageDays + " days, max " + maxAge + ")");
            }
        }
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
