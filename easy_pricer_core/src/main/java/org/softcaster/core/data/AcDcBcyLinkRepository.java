/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.softcaster.engine.enums.CurveRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Accesso alla tabella ac_dc_bcy_link. Le query sono derivate dai nomi dei
 * metodi (nessuna @Query necessaria).
 */
public interface AcDcBcyLinkRepository extends JpaRepository<AcDcBcyLink, Integer> {

    /**
     * La curva per asset class, valuta e ruolo: al massimo una (vincolo
     * univoco).
     * @param assetClass
     * @param currency
     * @param curveRole
     * @return 
     */
    Optional<AcDcBcyLink> findByAssetClassAndCurrencyAndCurveRole(
            Integer assetClass, Integer currency, CurveRole curveRole);

    /**
     * Tutti i ruoli configurati per una coppia asset class / valuta.
     * @param assetClass
     * @param currency
     * @return 
     */
    List<AcDcBcyLink> findByAssetClassAndCurrency(Integer assetClass, Integer currency);

    /**
     * Tutti i collegamenti che usano una yield_curve (es. prima di
     * cancellarla).
     * @param yieldCurve
     * @return 
     */
    List<AcDcBcyLink> findByYieldCurve(Integer yieldCurve);

    /**
     * Tutti i collegamenti che usano una spread_curve (es. prima di
     * cancellarla).
     * @param spreadCurve
     * @return 
     */
    List<AcDcBcyLink> findBySpreadCurve(Integer spreadCurve);
}
