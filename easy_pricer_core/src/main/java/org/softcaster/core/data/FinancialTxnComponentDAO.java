/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author softc
 */
@Service("financialTxnComponentDAO")
public class FinancialTxnComponentDAO {
    @Resource
    private FinancialTxnComponentRepository repository;

    @Transactional(readOnly = true)
    public FinancialTxnComponent findByTxnComponentId(Integer id) {
        return repository.findByTxnComponentId(id);
    }

    @Transactional
    public FinancialTxnComponent saveOrUpdate(FinancialTxnComponent financialTxnComponent) {
        return repository.save(financialTxnComponent);
    }

    @Transactional
    public void delete(FinancialTxnComponent financialTxnComponent) {
        repository.delete(financialTxnComponent);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxnComponent> findAll() {
        return repository.findAll();
    }
    
}
