package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Superfici di volatilita', loro punti e forward per scadenza.
 *
 * volatility_point e volatility_forward contengono solo la superficie
 * corrente (l'ultima rilevazione), senza data: la data e' in testata
 * (volatility_surface.reference_date) e i punti e forward letti la riportano
 * (getReferenceDate). Le rilevazioni precedenti stanno in
 * volatility_point_hist e volatility_forward_hist con la loro data.
 * Le letture a una data usano la corrente se non e' successiva alla data,
 * altrimenti l'ultima storica non successiva: per chi legge non cambia nulla.
 *
 * Scritture:
 *  - replacePoints: rilevazione completa (punti per strike, caricamento REST);
 *  - updateTenorPoints: aggiornamento per tenor (griglie per delta), i tenor
 *    non presenti restano;
 *  - replaceForwards: forward della rilevazione, dopo i punti della stessa data.
 * Una rilevazione piu' recente della corrente sposta prima la corrente (punti
 * e forward) nello storico; una piu' vecchia va direttamente nello storico.
 */
@Component
public class VolatilitySurfaceDAO {

    private final VolatilitySurfaceRepository surfaceRepository;
    private final VolatilityPointRepository pointRepository;
    private final VolatilityPointHistRepository histRepository;
    private final VolatilityForwardRepository forwardRepository;
    private final VolatilityForwardHistRepository forwardHistRepository;

    public VolatilitySurfaceDAO(VolatilitySurfaceRepository surfaceRepository, VolatilityPointRepository pointRepository,
            VolatilityPointHistRepository histRepository, VolatilityForwardRepository forwardRepository,
            VolatilityForwardHistRepository forwardHistRepository) {
        this.surfaceRepository = Objects.requireNonNull(surfaceRepository);
        this.pointRepository = Objects.requireNonNull(pointRepository);
        this.histRepository = Objects.requireNonNull(histRepository);
        this.forwardRepository = Objects.requireNonNull(forwardRepository);
        this.forwardHistRepository = Objects.requireNonNull(forwardHistRepository);
    }

    @Transactional(readOnly = true)
    public List<VolatilitySurfaceEntity> findAll() {
        return surfaceRepository.findAllOrdered();
    }

    @Transactional(readOnly = true)
    public Optional<VolatilitySurfaceEntity> findByCode(String code) {
        return surfaceRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<VolatilitySurfaceEntity> findByUnderlying(Integer underlyingId) {
        return surfaceRepository.findByUnderlying(underlyingId);
    }

    @Transactional
    public VolatilitySurfaceEntity saveOrUpdate(VolatilitySurfaceEntity surface) {
        return surfaceRepository.save(surface);
    }

    /**
     * Ultima data di rilevazione non successiva a date, corrente o storica.
     */
    @Transactional(readOnly = true)
    public Optional<LocalDate> findLastReferenceDate(Integer surfaceId, LocalDate date) {
        Optional<LocalDate> current = currentDate(surfaceId, date);
        return current.isPresent() ? current : histRepository.findLastReferenceDate(surfaceId, date);
    }

    /**
     * Punti dell'ultima rilevazione non successiva a date (vuoto se non ce ne
     * sono): la superficie corrente se non e' successiva a date, altrimenti
     * lo storico.
     */
    @Transactional(readOnly = true)
    public List<? extends AbstractVolatilityPoint> findLatestPoints(Integer surfaceId, LocalDate date) {
        Optional<LocalDate> current = currentDate(surfaceId, date);
        if (current.isPresent()) {
            List<VolatilityPoint> points = pointRepository.findBySurface(surfaceId);
            points.forEach(p -> p.setReferenceDate(current.get()));
            return points;
        }
        return histRepository.findLastReferenceDate(surfaceId, date)
                .map(last -> histRepository.findByReferenceDate(surfaceId, last))
                .orElse(List.of());
    }

    /**
     * Forward per scadenza dell'ultima rilevazione non successiva a date
     * (vuoto se non ce ne sono), ordinati per scadenza.
     */
    @Transactional(readOnly = true)
    public List<? extends AbstractVolatilityForward> findLatestForwards(Integer surfaceId, LocalDate date) {
        Optional<LocalDate> current = currentDate(surfaceId, date);
        if (current.isPresent()) {
            List<VolatilityForward> forwards = forwardRepository.findBySurface(surfaceId);
            if (!forwards.isEmpty()) {
                forwards.forEach(f -> f.setReferenceDate(current.get()));
                return forwards;
            }
        }
        return forwardHistRepository.findLastReferenceDate(surfaceId, date)
                .map(last -> forwardHistRepository.findByReferenceDate(surfaceId, last))
                .orElse(List.of());
    }

    /**
     * Rilevazione completa: sostituisce la superficie corrente (se la data e'
     * la stessa o piu' recente) o i punti storici della data (se precedente).
     */
    @Transactional
    public void replacePoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        checkPoints(surfaceId, referenceDate, points);
        Optional<LocalDate> current = pointRepository.findReferenceDate(surfaceId);
        if (current.isPresent() && referenceDate.isBefore(current.get())) {
            replaceHistory(surfaceId, referenceDate, points);
            return;
        }
        if (current.isPresent() && referenceDate.isAfter(current.get())) {
            archive(surfaceId, current.get());
        }
        pointRepository.deleteBySurface(surfaceId);
        pointRepository.flush();
        pointRepository.saveAll(points);
        pointRepository.updateReferenceDate(surfaceId, referenceDate);
    }

    /**
     * Aggiornamento per tenor (punti quotati per delta): i punti dei tenor
     * presenti sostituiscono quelli correnti degli stessi tenor, gli altri
     * restano; i punti correnti senza tenor (quotati per strike) si tolgono,
     * una superficie e' per delta o per strike. Con una data piu' recente la
     * superficie corrente va prima nello storico e passa tutta alla nuova data
     * (scartati i punti gia' scaduti): la superficie corrente ha sempre una
     * sola data di rilevazione. Con una data precedente alla corrente i punti
     * sostituiscono la rilevazione storica di quella data.
     */
    @Transactional
    public void updateTenorPoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        checkPoints(surfaceId, referenceDate, points);
        if (points.stream().anyMatch(p -> p.getTenor() == null)) {
            throw new IllegalArgumentException("Update by tenor with points without tenor");
        }
        Optional<LocalDate> current = pointRepository.findReferenceDate(surfaceId);
        if (current.isPresent() && referenceDate.isBefore(current.get())) {
            replaceHistory(surfaceId, referenceDate, points);
            return;
        }
        if (current.isPresent() && referenceDate.isAfter(current.get())) {
            archive(surfaceId, current.get());
            pointRepository.deleteExpired(surfaceId, referenceDate);
            pointRepository.flush();
        }
        List<String> tenors = points.stream().map(VolatilityPoint::getTenor).distinct().toList();
        pointRepository.deleteByTenors(surfaceId, tenors);
        pointRepository.flush();
        pointRepository.saveAll(points);
        pointRepository.updateReferenceDate(surfaceId, referenceDate);
    }

    /**
     * Forward di una rilevazione, da salvare dopo i punti della stessa data:
     * sostituiscono i forward correnti se la data e' quella della superficie
     * corrente, i forward storici della data se precedente.
     *
     * @throws IllegalStateException con una data successiva alla superficie corrente
     */
    @Transactional
    public void replaceForwards(Integer surfaceId, LocalDate referenceDate, List<VolatilityForward> forwards) {
        for (VolatilityForward f : forwards) {
            if (!surfaceId.equals(f.getVolatilitySurface()) || !referenceDate.equals(f.getReferenceDate())) {
                throw new IllegalArgumentException("Forward of another surface or reference date: "
                        + f.getVolatilitySurface() + " " + f.getReferenceDate());
            }
        }
        Optional<LocalDate> current = pointRepository.findReferenceDate(surfaceId);
        if (current.isEmpty() || referenceDate.isAfter(current.get())) {
            throw new IllegalStateException("Forwards as of " + referenceDate + " without the volatility points of the same date"
                    + " (current surface as of " + current.orElse(null) + ")");
        }
        if (referenceDate.isBefore(current.get())) {
            forwardHistRepository.deleteByReferenceDate(surfaceId, referenceDate);
            forwardHistRepository.flush();
            forwardHistRepository.saveAll(forwards.stream().map(f -> VolatilityForwardHist.of(f, referenceDate)).toList());
            return;
        }
        forwardRepository.deleteBySurface(surfaceId);
        forwardRepository.flush();
        forwardRepository.saveAll(forwards);
    }

    /**
     * La superficie corrente (punti e forward, data date) nello storico, al
     * posto di una eventuale rilevazione storica della stessa data; i forward
     * correnti si tolgono (sono della data archiviata).
     */
    private void archive(Integer surfaceId, LocalDate date) {
        histRepository.deleteByReferenceDate(surfaceId, date);
        histRepository.flush();
        histRepository.saveAll(pointRepository.findBySurface(surfaceId).stream()
                .map(p -> VolatilityPointHist.of(p, date)).toList());
        histRepository.flush();

        List<VolatilityForward> forwards = forwardRepository.findBySurface(surfaceId);
        if (!forwards.isEmpty()) {
            forwardHistRepository.deleteByReferenceDate(surfaceId, date);
            forwardHistRepository.flush();
            forwardHistRepository.saveAll(forwards.stream().map(f -> VolatilityForwardHist.of(f, date)).toList());
            forwardHistRepository.flush();
            forwardRepository.deleteBySurface(surfaceId);
            forwardRepository.flush();
        }
    }

    private void replaceHistory(Integer surfaceId, LocalDate date, List<VolatilityPoint> points) {
        histRepository.deleteByReferenceDate(surfaceId, date);
        histRepository.flush();
        histRepository.saveAll(points.stream().map(p -> VolatilityPointHist.of(p, date)).toList());
    }

    /**
     * Data della superficie corrente se non successiva a date.
     */
    private Optional<LocalDate> currentDate(Integer surfaceId, LocalDate date) {
        return pointRepository.findReferenceDate(surfaceId).filter(d -> !d.isAfter(date));
    }

    private static void checkPoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No volatility points to save");
        }
        for (VolatilityPoint p : points) {
            if (!surfaceId.equals(p.getVolatilitySurface()) || !referenceDate.equals(p.getReferenceDate())) {
                throw new IllegalArgumentException("Point of another surface or reference date: "
                        + p.getVolatilitySurface() + " " + p.getReferenceDate());
            }
        }
    }
}
