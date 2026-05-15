package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    public Country findByIdCountry(Integer idCountry);

    public Country findByAlfa3Code(String alfa3Code);
}
