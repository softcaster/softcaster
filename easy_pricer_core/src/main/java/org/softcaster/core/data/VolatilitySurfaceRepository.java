package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolatilitySurfaceRepository extends JpaRepository<VolatilitySurfaceEntity, Integer> {

    Optional<VolatilitySurfaceEntity> findByCode(String code);

    @Query("""
        SELECT s
        FROM VolatilitySurfaceEntity s
        WHERE s.underlying.idMasterData = :underlying
        """)
    Optional<VolatilitySurfaceEntity> findByUnderlying(@Param("underlying") Integer underlying);

    @Query("""
        SELECT s
        FROM VolatilitySurfaceEntity s
        ORDER BY s.code
        """)
    List<VolatilitySurfaceEntity> findAllOrdered();
}
