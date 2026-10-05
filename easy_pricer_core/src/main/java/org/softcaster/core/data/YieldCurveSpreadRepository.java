/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YieldCurveSpreadRepository extends JpaRepository<YieldCurveSpread, Integer> {

    @Query("select s from YieldCurveSpread s where s.yieldCurve.idYieldCurve = :id "
            + "and s.asOfDate = (select max(x.asOfDate) from YieldCurveSpread x "
            + "where x.yieldCurve.idYieldCurve = :id and x.asOfDate <= :date)")
    List<YieldCurveSpread> findLatest(@Param("id") Integer id, @Param("date") LocalDate date);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from YieldCurveSpread s where s.yieldCurve.idYieldCurve = :id and s.asOfDate = :date")
    void deleteByCurveAndDate(@Param("id") Integer id, @Param("date") LocalDate date);
}
