package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Calibrazione corrente (yield_curve_spread) e data in testata
 * (spread_curve.as_of_date).
 */
public interface YieldCurveSpreadRepository extends JpaRepository<YieldCurveSpread, Integer> {

    @Query("select s from YieldCurveSpread s where s.spreadCurveId = :id order by s.offsetType, s.offsetValue")
    List<YieldCurveSpread> findByCurve(@Param("id") Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from YieldCurveSpread s where s.spreadCurveId = :id")
    void deleteByCurve(@Param("id") Integer id);

    /**
     * Data della calibrazione corrente (vuoto se la curva non e' mai stata calibrata).
     */
    @Query("select c.asOfDate from SpreadCurveEntity c where c.idSpreadCurve = :id")
    Optional<LocalDate> findAsOfDate(@Param("id") Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SpreadCurveEntity c set c.asOfDate = :date where c.idSpreadCurve = :id")
    void updateAsOfDate(@Param("id") Integer id, @Param("date") LocalDate date);
}
