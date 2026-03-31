package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("currencyPairDAO")
public class CurrencyPairDAO {

    @Resource
    private CurrencyPairRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");
    
    @Transactional(readOnly = true)
    public CurrencyPair findByIdCurrencyPair(Integer idCurrencyPair) {
        return repository.findByIdCurrencyPair(idCurrencyPair);
    }

    @Transactional(readOnly = true)
    public List<CurrencyPair> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public CurrencyPair saveOrUpdate(CurrencyPair currencyPair) {
        return repository.save(currencyPair);
    }

    @Transactional
    public void delete(CurrencyPair currencyPair) {
        repository.delete(currencyPair);
    }

}
