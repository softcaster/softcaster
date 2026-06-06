/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author softc
 */
public interface FinancialTxnComponentRepository extends JpaRepository<FinancialTxnComponent, Integer> {

    public FinancialTxnComponent findByTxnComponentId(Integer id);
    
    
}
