package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YieldCurveRepository extends JpaRepository<YieldCurve, Integer> {

    public YieldCurve findByIdYieldCurve(Integer idYieldCurve);

    /*    @Query("""
        select yc from YieldCurve yc 
        join fetch yc.currency 
        join fetch yc.calendar 
        where yc.code = :code
    """)
    public YieldCurve findByCode(@Param("code") String code);*/
    @EntityGraph(attributePaths = {"currency,calendar"})
    public YieldCurve findByCode(String code);

    @Query(value = "SELECT code FROM yield_curve ORDER BY code", nativeQuery = true)
    public List<String> findNames();
}
