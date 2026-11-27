package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YieldCurveSpreadHistRepository extends JpaRepository<YieldCurveSpreadHist, Integer> {

    /**
     * Ultima data di calibrazione storica non successiva a date.
     */
    @Query("select max(s.asOfDate) from YieldCurveSpreadHist s where s.spreadCurveId = :id and s.asOfDate <= :date")
    Optional<LocalDate> findLastAsOfDate(@Param("id") Integer id, @Param("date") LocalDate date);

    @Query("select s from YieldCurveSpreadHist s where s.spreadCurveId = :id and s.asOfDate = :date "
            + "order by s.offsetType, s.offsetValue")
    List<YieldCurveSpreadHist> findByAsOfDate(@Param("id") Integer id, @Param("date") LocalDate date);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from YieldCurveSpreadHist s where s.spreadCurveId = :id and s.asOfDate = :date")
    void deleteByAsOfDate(@Param("id") Integer id, @Param("date") LocalDate date);
}
