package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolatilityPointRepository extends JpaRepository<VolatilityPoint, Integer> {

    /**
     * Ultima data di rilevazione non successiva a date.
     */
    @Query("""
        SELECT MAX(p.referenceDate)
        FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface AND p.referenceDate <= :date
        """)
    Optional<LocalDate> findLastReferenceDate(@Param("surface") Integer surface, @Param("date") LocalDate date);

    @Query("""
        SELECT p
        FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface AND p.referenceDate = :date
        ORDER BY p.expiryDate, p.strike
        """)
    List<VolatilityPoint> findByReferenceDate(@Param("surface") Integer surface, @Param("date") LocalDate date);

    /**
     * Punti correnti della superficie (una sola data di rilevazione).
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
}
