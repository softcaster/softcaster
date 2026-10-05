/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("yieldCurveSpreadDAO")
public class YieldCurveSpreadDAO {

    protected final YieldCurveSpreadRepository repository;

    public YieldCurveSpreadDAO(YieldCurveSpreadRepository repository) {
        this.repository = repository;
    }
    
    @Transactional
    public void deleteByCurveAndDate(Integer idYieldCurve, LocalDate officialDate) {
        repository.deleteByCurveAndDate(idYieldCurve, officialDate);
    }

    @Transactional
    public void saveAll(List<YieldCurveSpread> rows) {
        repository.saveAll(rows);
    }

    @Transactional(readOnly = true)
    public List<YieldCurveSpread> findLatest(Integer idYieldCurve, LocalDate officialDate) {
        return repository.findLatest(idYieldCurve, officialDate);
    }
}
