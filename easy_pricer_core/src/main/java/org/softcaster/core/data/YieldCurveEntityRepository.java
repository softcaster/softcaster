package org.softcaster.core.data;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YieldCurveEntityRepository extends JpaRepository<YieldCurveEntity, Integer> {

    public YieldCurveEntity findByIdYieldCurve(Integer idYieldCurve);

    @EntityGraph(attributePaths = {
        "currency",
        "calendar",
        "items"
    })
    public YieldCurveEntity findByCode(String code);

    // Caricamento batch di un gruppo di curve
    @EntityGraph(attributePaths = {
        "currency",
        "calendar",
        "items"
    })
    List<YieldCurveEntity> findByCodeIn(Collection<String> codes);

    @Query(value = "SELECT code FROM yield_curve ORDER BY code", nativeQuery = true)
    public List<String> findNames();

    @Query("select count(c) > 0 from YieldCurveEntity c where c.code = :code")
    boolean existsByCode(@Param("code") String code);
}
