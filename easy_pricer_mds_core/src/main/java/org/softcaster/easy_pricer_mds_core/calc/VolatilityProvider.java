package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.softcaster.core.data.AbstractVolatilityPoint;
import org.softcaster.core.data.VolatilityForward;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.engine.volatility.GridVolatilitySurface;
import org.softcaster.engine.volatility.VolatilityNode;
import org.softcaster.engine.volatility.VolatilitySurface;

/**
 * Superficie di volatilita' di un sottostante a una data di valutazione:
 * punti dell'ultima rilevazione non successiva alla data (superficie corrente
 * o storico, VolatilitySurfaceDAO), tempo misurato
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
        List<? extends AbstractVolatilityPoint> points = volatilitySurfaceDAO.findLatestPoints(entity.get().getIdVolatilitySurface(), valuationDate);
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

    /**
     * Forward del sottostante a una scadenza dai forward rilevati con la
     * superficie (ultima rilevazione non successiva a valuationDate), vuoto
     * se il sottostante non ha superficie o forward, o se la scadenza e' oltre
     * l'ultimo forward senza un livello dell'indice per estendere.
     *
     * Interpolazione lineare nella data di scadenza tra i due forward vicini;
     * prima del primo, tra il livello dell'indice all'import (alla data di
     * rilevazione) e il primo forward; oltre l'ultimo, al tasso di carry
     * implicito dell'ultimo, ln(F / spot) / t. Con un livello corrente diverso
     * da quello all'import il forward si riporta in proporzione,
     * F x currentSpot / spot.
     *
     * @param underlyingId sottostante della superficie
     * @param valuationDate data di valutazione
     * @param expiry scadenza dell'opzione
     * @param currentSpot livello corrente del sottostante, null per usare quello all'import
     * @return
     * @throws IllegalStateException se i forward sono piu' vecchi di max_age_days della superficie
     */
    public Optional<Double> forward(Integer underlyingId, LocalDate valuationDate, LocalDate expiry, Double currentSpot) {
        Optional<VolatilitySurfaceEntity> entity = volatilitySurfaceDAO.findByUnderlying(underlyingId);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        List<VolatilityForward> forwards = volatilitySurfaceDAO.findLatestForwards(entity.get().getIdVolatilitySurface(), valuationDate);
        if (forwards.isEmpty()) {
            return Optional.empty();
        }
        LocalDate asOf = forwards.get(0).getReferenceDate();
        long ageDays = ChronoUnit.DAYS.between(asOf, valuationDate);
        Integer maxAge = entity.get().getMaxAgeDays();
        if (maxAge != null && ageDays > maxAge) {
            throw new IllegalStateException("Forwards of volatility surface " + entity.get().getCode() + " are stale: as of "
                    + asOf + " (" + ageDays + " days, max " + maxAge + ")");
        }
        Double spotRef = forwards.stream().map(VolatilityForward::getSpot).filter(s -> s != null && s > 0)
                .findFirst().orElse(null);

        Double forward = interpolate(forwards, asOf, expiry, spotRef);
        if (forward == null) {
            return Optional.empty();
        }
        if (spotRef != null && currentSpot != null && currentSpot > 0) {
            forward *= currentSpot / spotRef;
        }
        return Optional.of(forward);
    }

    private static Double interpolate(List<VolatilityForward> forwards, LocalDate asOf, LocalDate expiry, Double spotRef) {
        VolatilityForward before = null;
        VolatilityForward after = null;
        for (VolatilityForward f : forwards) {
            if (f.getExpiryDate().equals(expiry)) {
                return f.getForward();
            }
            if (f.getExpiryDate().isBefore(expiry)) {
                before = f;
            } else if (after == null) {
                after = f;
            }
        }
        if (before != null && after != null) {
            return linear(before.getExpiryDate(), before.getForward(), after.getExpiryDate(), after.getForward(), expiry);
        }
        if (after != null) {
            // prima del primo forward: dal livello all'import (t = 0) al primo
            return spotRef == null ? after.getForward() : linear(asOf, spotRef, after.getExpiryDate(), after.getForward(), expiry);
        }
        // oltre l'ultimo: carry implicito dell'ultimo forward
        if (spotRef == null) {
            return null;
        }
        double tLast = ChronoUnit.DAYS.between(asOf, before.getExpiryDate()) / 365.0;
        double t = ChronoUnit.DAYS.between(asOf, expiry) / 365.0;
        double carry = Math.log(before.getForward() / spotRef) / tLast;
        return spotRef * Math.exp(carry * t);
    }

    private static double linear(LocalDate d0, double v0, LocalDate d1, double v1, LocalDate d) {
        double span = ChronoUnit.DAYS.between(d0, d1);
        return span <= 0 ? v1 : v0 + (v1 - v0) * ChronoUnit.DAYS.between(d0, d) / span;
    }
}
