package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Superfici di volatilita' e loro punti per data di rilevazione.
 */
@Component
public class VolatilitySurfaceDAO {

    private final VolatilitySurfaceRepository surfaceRepository;
    private final VolatilityPointRepository pointRepository;

    public VolatilitySurfaceDAO(VolatilitySurfaceRepository surfaceRepository, VolatilityPointRepository pointRepository) {
        this.surfaceRepository = Objects.requireNonNull(surfaceRepository);
        this.pointRepository = Objects.requireNonNull(pointRepository);
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

    @Transactional(readOnly = true)
    public Optional<LocalDate> findLastReferenceDate(Integer surfaceId, LocalDate date) {
        return pointRepository.findLastReferenceDate(surfaceId, date);
    }

    /**
     * Punti dell'ultima rilevazione non successiva a date (vuoto se non ce ne
     * sono).
     */
    @Transactional(readOnly = true)
    public List<VolatilityPoint> findLatestPoints(Integer surfaceId, LocalDate date) {
        return pointRepository.findLastReferenceDate(surfaceId, date)
                .map(last -> pointRepository.findByReferenceDate(surfaceId, last))
                .orElse(List.of());
    }

    @Transactional
    public VolatilitySurfaceEntity saveOrUpdate(VolatilitySurfaceEntity surface) {
        return surfaceRepository.save(surface);
    }

    /**
     * Sostituisce i punti di una data di rilevazione.
     */
    @Transactional
    public List<VolatilityPoint> replacePoints(Integer surfaceId, LocalDate referenceDate, List<VolatilityPoint> points) {
        pointRepository.deleteByReferenceDate(surfaceId, referenceDate);
        pointRepository.flush();
        return pointRepository.saveAll(points);
    }
}
