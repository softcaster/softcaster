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

    @Modifying
    @Query("""
        DELETE FROM VolatilityPoint p
        WHERE p.volatilitySurface = :surface AND p.referenceDate = :date
        """)
    int deleteByReferenceDate(@Param("surface") Integer surface, @Param("date") LocalDate date);
}
