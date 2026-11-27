package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolatilityForwardRepository extends JpaRepository<VolatilityForward, Integer> {

    @Query("""
        SELECT max(f.referenceDate) FROM VolatilityForward f
        WHERE f.volatilitySurface = :surface AND f.referenceDate <= :date
        """)
    Optional<LocalDate> findLastReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);

    @Query("""
        SELECT f FROM VolatilityForward f
        WHERE f.volatilitySurface = :surface AND f.referenceDate = :date
        ORDER BY f.expiryDate
        """)
    List<VolatilityForward> findByReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);

    @Modifying
    @Query("DELETE FROM VolatilityForward f WHERE f.volatilitySurface = :surface AND f.referenceDate = :date")
    void deleteByReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);
}
