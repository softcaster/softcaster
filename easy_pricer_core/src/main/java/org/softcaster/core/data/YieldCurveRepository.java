package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface YieldCurveRepository extends JpaRepository<YieldCurve, Integer> {

    public YieldCurve findByIdYieldCurve(Integer idYieldCurve);

    public YieldCurve findByCode(String code);

    @Query(value = "SELECT code FROM yield_curve ORDER BY code", nativeQuery = true)
    public List<String> findNames();
}
