/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.enums.LoadType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GranularCurveRepository extends JpaRepository<GranularCurve, Long> {

    List<GranularCurve> findByBusinessDateAndMarketAndLoadTypeOrderByDeliveryDate(
            LocalDate businessDate, String market, LoadType loadType
    );

    List<GranularCurve> findByBusinessDateAndMarketAndLoadTypeAndDeliveryDateBetweenOrderByDeliveryDate(
            LocalDate businessDate, String market, LoadType loadType,
            LocalDate deliveryStart, LocalDate deliveryEnd
    );
}
