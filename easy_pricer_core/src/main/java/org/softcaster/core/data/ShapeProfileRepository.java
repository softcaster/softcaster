/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.enums.LoadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShapeProfileRepository extends JpaRepository<ShapeProfile, Integer> {

    @Query("""
        SELECT sp FROM ShapeProfile sp
        WHERE sp.profileCode = :profileCode
          AND sp.market = :market
          AND sp.loadType = :loadType
          AND sp.validFrom <= :referenceDate
          AND (sp.validTo IS NULL OR sp.validTo >= :referenceDate)
        """)
    List<ShapeProfile> findApplicable(
            @Param("profileCode") String profileCode,
            @Param("market") String market,
            @Param("loadType") LoadType loadType,
            @Param("referenceDate") LocalDate referenceDate
    );
}
