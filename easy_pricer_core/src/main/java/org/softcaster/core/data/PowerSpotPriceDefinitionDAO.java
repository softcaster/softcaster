/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PowerSpotPriceDefinitionDAO {
    
    private final PowerSpotPriceDefinitionRepository repository;
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");
    
    public PowerSpotPriceDefinitionDAO(PowerSpotPriceDefinitionRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public PowerSpotPriceDefinition saveOrUpdate(PowerSpotPriceDefinition mqd) {
        return repository.save(mqd);
    }

    @Transactional
    public void delete(PowerSpotPriceDefinition mqd) {
        repository.delete(mqd);
    }

    @Transactional(readOnly = true)
    public List<PowerSpotPriceDefinition> findAll() {
        return repository.findAll(sortByCode);
    }
    
    @Transactional(readOnly = true)
    public PowerSpotPriceDefinition findByIdWithCountry(Integer id){
        return repository.findByIdWithCountry(id);
    }
    
    @Transactional(readOnly = true)
    public PowerSpotPriceDefinition findByCodeWithCountry(String code){
        return repository.findByCodeWithCountry(code);
    }

    @Transactional(readOnly = true)
    public PowerSpotPriceDefinition findByCodeWithQuotes(String code){
        return repository.findByCodeWithQuotes(code);
    }
}
