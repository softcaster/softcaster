/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MarketQuoteDAO {

    private final MarketQuoteRepository repository;
    private final Sort sortByBusinessDate = Sort.by(Sort.Direction.ASC, "businessDate");

    public MarketQuoteDAO(MarketQuoteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MarketQuote saveOrUpdate(MarketQuote mq) {
        return repository.save(mq);
    }

    @Transactional
    public void delete(MarketQuote mq) {
        repository.delete(mq);
    }

    @Transactional(readOnly = true)
    public List<MarketQuote> findAll() {
        return repository.findAll(sortByBusinessDate);
    }
    
    @Transactional(readOnly = true)
    public MarketQuote findByMarketQuoteIdAndBusinessDate(Integer marketQuoteId, LocalDate businessDate) {
        return repository.findByMarketQuoteIdAndBusinessDate(marketQuoteId, businessDate);
    }
}
