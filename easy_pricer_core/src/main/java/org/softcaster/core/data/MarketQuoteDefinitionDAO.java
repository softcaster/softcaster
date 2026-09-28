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
public class MarketQuoteDefinitionDAO {
  
    private final MarketQuoteDefinitionRepository repository;
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");
    
    public MarketQuoteDefinitionDAO(MarketQuoteDefinitionRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public MarketQuoteDefinition saveOrUpdate(MarketQuoteDefinition mqd) {
        return repository.save(mqd);
    }

    @Transactional
    public void delete(MarketQuoteDefinition mqd) {
        repository.delete(mqd);
    }

    @Transactional(readOnly = true)
    public List<MarketQuoteDefinition> findAll() {
        return repository.findAll(sortByCode);
    }
    
    @Transactional(readOnly = true)
    public MarketQuoteDefinition findByIdWithCountry(Integer id){
        return repository.findByIdWithCountry(id);
    }
}
