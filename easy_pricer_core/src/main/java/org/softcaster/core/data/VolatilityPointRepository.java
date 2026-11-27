package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Punti della superficie corrente (volatility_point, senza data) e data in
 * testata (volatility_surface.reference_date).
 */
public interface VolatilityPointRepository extends JpaRepository<VolatilityPoint, Integer> {

    /**
     * Punti correnti della superficie.
     */
    @Query("""
        SELECT p
        FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface
        ORDER BY p.expiryDate, p.strike
        """)
    List<VolatilityPoint> findBySurface(@Param("surface") Integer surface);

    @Modifying
    @Query("""
        DELETE FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface
        """)
    int deleteBySurface(@Param("surface") Integer surface);

    /**
     * Punti dei tenor indicati e punti senza tenor (quotati per strike).
     */
    @Modifying
    @Query("""
        DELETE FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface AND (p.tenor IN :tenors OR p.tenor IS NULL)
        """)
    int deleteByTenors(@Param("surface") Integer surface, @Param("tenors") List<String> tenors);

    /**
     * Punti correnti scaduti a date (scadenza non successiva).
     */
    @Modifying
    @Query("""
        DELETE FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface AND p.expiryDate <= :date
        """)
    int deleteExpired(@Param("surface") Integer surface, @Param("date") LocalDate date);

    /**
     * Data della superficie corrente (vuoto se la superficie non ha punti).
     */
    @Query("SELECT s.referenceDate FROM VolatilitySurfaceEntity s WHERE s.idVolatilitySurface = :surface")
    Optional<LocalDate> findReferenceDate(@Param("surface") Integer surface);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE VolatilitySurfaceEntity s SET s.referenceDate = :date WHERE s.idVolatilitySurface = :surface")
    int updateReferenceDate(@Param("surface") Integer surface, @Param("date") LocalDate date);
}
