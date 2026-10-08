/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("spreadCurveEntityDAO")
public class SpreadCurveEntityDAO {

    protected final SpreadCurveEntityRepository repository;

    protected SpreadCurveEntityDAO(SpreadCurveEntityRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SpreadCurveEntity findByCodeWithBase(String code) {
        return repository.findByCodeWithBase(code);
    }

    @Transactional(readOnly = true)
    public SpreadCurveEntity findByIdWithBase(Integer id) {
        return repository.findByIdWithBase(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
    @Transactional(readOnly = true)
    public List<String> findNames() {
        return repository.findNames();
    }

    @Transactional(readOnly = true)
    public SpreadCurveEntity findByCode(String code) {
        return repository.findByCode(code);
    }
}
