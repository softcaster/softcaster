package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("countryDAO")
public class CountryDAO {

    @Resource
    private CountryRepository repository;
    
    private final Sort sortByAlfa3Code = Sort.by(Sort.Direction.ASC, "alfa3Code");

    @Transactional(readOnly = true)
    public Country findByIdCountry(Integer idCountry) {
        return repository.findByIdCountry(idCountry);
    }

    @Transactional
    public Country saveOrUpdate(Country country) {
        return repository.save(country);
    }

    @Transactional
    public void delete(Country country) {
        repository.delete(country);
    }

    @Transactional(readOnly = true)
    public List<Country> findAll() {
        return repository.findAll(sortByAlfa3Code);
    }

    @Transactional(readOnly = true)
    public Country findByAlfa3Code(String alfa3Code) {
        return repository.findByAlfa3Code(alfa3Code);
    }
}
