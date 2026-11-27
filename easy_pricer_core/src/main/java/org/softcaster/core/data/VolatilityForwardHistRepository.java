package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolatilityForwardHistRepository extends JpaRepository<VolatilityForwardHist, Integer> {

    @Query("""
        SELECT max(f.referenceDate) FROM VolatilityForwardHist f
        WHERE f.volatilitySurface = :surface AND f.referenceDate <= :date
        """)
    Optional<LocalDate> findLastReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);

    @Query("""
        SELECT f FROM VolatilityForwardHist f
        WHERE f.volatilitySurface = :surface AND f.referenceDate = :date
        ORDER BY f.expiryDate
        """)
    List<VolatilityForwardHist> findByReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);

    @Modifying
    @Query("DELETE FROM VolatilityForwardHist f WHERE f.volatilitySurface = :surface AND f.referenceDate = :date")
    int deleteByReferenceDate(@Param("surface") Integer surfaceId, @Param("date") LocalDate date);
}
