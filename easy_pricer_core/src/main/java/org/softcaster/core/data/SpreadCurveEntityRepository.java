/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author ep
 */
public interface SpreadCurveEntityRepository extends JpaRepository<SpreadCurveEntity, Integer> {

    @Query("select c from SpreadCurveEntity c join fetch c.baseCurve where c.code = :code")
    SpreadCurveEntity findByCodeWithBase(@Param("code") String code);

    @Query("select c from SpreadCurveEntity c join fetch c.baseCurve where c.idSpreadCurve= :id")
    public SpreadCurveEntity findByIdWithBase(@Param("id") Integer id);
}
