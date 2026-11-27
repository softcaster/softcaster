package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Forward correnti (volatility_forward, una sola rilevazione per superficie).
 */
public interface VolatilityForwardRepository extends JpaRepository<VolatilityForward, Integer> {

    @Query("""
        SELECT f FROM VolatilityForward f
        WHERE f.volatilitySurface = :surface
        ORDER BY f.expiryDate
        """)
    List<VolatilityForward> findBySurface(@Param("surface") Integer surfaceId);

    @Modifying
    @Query("DELETE FROM VolatilityForward f WHERE f.volatilitySurface = :surface")
    int deleteBySurface(@Param("surface") Integer surfaceId);
}
