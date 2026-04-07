package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("currencyDAO")
public class CurrencyDAO {

    @Resource
    private CurrencyRepository repository;
    
    private final Sort sortByIsoCode = Sort.by(Sort.Direction.ASC, "isoCode");

    @Transactional(readOnly = true)
    public Currency findByIdCurrency(Integer idCurrency) {
        return repository.findByIdCurrency(idCurrency);
    }

    @Transactional(readOnly = true)
    public Currency findByIsoCode(String isoCode) {
        return repository.findByIsoCode(isoCode);
    }
    @Transactional
    public Currency saveOrUpdate(Currency currency) {
        return repository.save(currency);
    }

    @Transactional
    public void delete(Currency currency) {
        repository.delete(currency);
    }

    @Transactional(readOnly = true)
    public List<Currency> findAll() {
        return repository.findAll(sortByIsoCode);
    }
}
