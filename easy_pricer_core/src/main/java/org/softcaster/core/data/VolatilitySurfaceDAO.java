package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Superfici di volatilita' e loro punti.
 *
 * volatility_point contiene solo la superficie corrente (l'ultima
 * rilevazione); le rilevazioni precedenti stanno in volatility_point_hist.
 * Le letture a una data cercano prima nella corrente, poi nello storico:
 * per chi legge non cambia nulla.
 *
 * Scritture:
 *  - replacePoints: rilevazione completa (punti per strike, caricamento REST);
 *  - updateTenorPoints: aggiornamento per tenor (griglie per delta), i tenor
 *    non presenti restano.
 * Una rilevazione piu' recente della corrente sposta prima la corrente nello
 * storico; una piu' vecchia va direttamente nello storico.
 */
@Component
public class VolatilitySurfaceDAO {

    // per "nessun limite" nelle letture dell'ultima rilevazione
    private static final LocalDate ANY = LocalDate.of(9999, 12, 31);

    private final VolatilitySurfaceRepository surfaceRepository;
    private final VolatilityPointRepository pointRepository;
    private final VolatilityPointHistRepository histRepository;

    public VolatilitySurfaceDAO(VolatilitySurfaceRepository surfaceRepository, VolatilityPointRepository pointRepository,
            VolatilityPointHistRepository histRepository) {
        this.surfaceRepository = Objects.requireNonNull(surfaceRepository);
        this.pointRepository = Objects.requireNonNull(pointRepository);
        this.histRepository = Objects.requireNonNull(histRepository);
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

    /**
     * Ultima data di rilevazione non successiva a date, corrente o storica.
     */
    @Transactional(readOnly = true)
    public Optional<LocalDate> findLastReferenceDate(Integer surfaceId, LocalDate date) {
        Optional<LocalDate> current = pointRepository.findLastReferenceDate(surfaceId, date);
        return current.isPresent() ? current : histRepository.findLastReferenceDate(surfaceId, date);
    }

    /**
     * Punti dell'ultima rilevazione non successiva a date (vuoto se non ce ne
     * sono): la superficie corrente se non e' successiva a date, altrimenti
     * lo storico.
     */
    @Transactional(readOnly = true)
    public List<? extends AbstractVolatilityPoint> findLatestPoints(Integer surfaceId, LocalDate date) {
        Optional<LocalDate> current = pointRepository.findLastReferenceDate(surfaceId, date);
        if (current.isPresent()) {
            return pointRepository.findByReferenceDate(surfaceId, current.get());
        }
        return histRepository.findLastReferenceDate(surfaceId, date)
                .map(last -> histRepository.findByReferenceDate(surfaceId, last))
                .orElse(List.of());
    }

    @Transactional
    public VolatilitySurfaceEntity saveOrUpdate(VolatilitySurfaceEntity surface) {
        return surfaceRepository.save(surface);
    }

    /**
     * Rilevazione completa: sostituisce la superficie corrente (se la data e'
     * la stessa o piu' recente) o i punti storici della data (se precedente).
     */
    @Transactional
    public void replacePoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        checkPoints(surfaceId, referenceDate, points);
        Optional<LocalDate> current = pointRepository.findLastReferenceDate(surfaceId, ANY);
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
    }

    /**
     * Aggiornamento per tenor (punti quotati per delta): i punti dei tenor
     * presenti sostituiscono quelli correnti degli stessi tenor, gli altri
     * restano; i punti correnti senza tenor (quotati per strike) si tolgono,
     * una superficie e' per delta o per strike. Con una data piu' recente la superficie corrente va prima nello
     * storico e passa tutta alla nuova data (scartati i punti gia' scaduti):
     * la superficie corrente ha sempre una sola data di rilevazione. Con una
     * data precedente alla corrente i punti sostituiscono la rilevazione
     * storica di quella data.
     */
    @Transactional
    public void updateTenorPoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        checkPoints(surfaceId, referenceDate, points);
        if (points.stream().anyMatch(p -> p.getTenor() == null)) {
            throw new IllegalArgumentException("Update by tenor with points without tenor");
        }
        Optional<LocalDate> current = pointRepository.findLastReferenceDate(surfaceId, ANY);
        if (current.isPresent() && referenceDate.isBefore(current.get())) {
            replaceHistory(surfaceId, referenceDate, points);
            return;
        }
        if (current.isPresent() && referenceDate.isAfter(current.get())) {
            archive(surfaceId, current.get());
            for (VolatilityPoint p : pointRepository.findBySurface(surfaceId)) {
                if (p.getExpiryDate().isAfter(referenceDate)) {
                    p.moveTo(referenceDate);
                } else {
                    pointRepository.delete(p);
                }
            }
            pointRepository.flush();
        }
        List<String> tenors = points.stream().map(VolatilityPoint::getTenor).distinct().toList();
        pointRepository.deleteByTenors(surfaceId, tenors);
        pointRepository.flush();
        pointRepository.saveAll(points);
    }

    /**
     * La superficie corrente (data date) nello storico, al posto di una
     * eventuale rilevazione storica della stessa data.
     */
    private void archive(Integer surfaceId, LocalDate date) {
        histRepository.deleteByReferenceDate(surfaceId, date);
        histRepository.flush();
        histRepository.saveAll(pointRepository.findBySurface(surfaceId).stream().map(VolatilityPointHist::of).toList());
        histRepository.flush();
    }

    private void replaceHistory(Integer surfaceId, LocalDate date, List<VolatilityPoint> points) {
        histRepository.deleteByReferenceDate(surfaceId, date);
        histRepository.flush();
        histRepository.saveAll(points.stream().map(VolatilityPointHist::of).toList());
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
