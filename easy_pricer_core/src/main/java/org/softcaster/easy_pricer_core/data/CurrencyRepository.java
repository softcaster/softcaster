package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    public Currency findByIdCurrency(Integer idCurrency);

    public Currency findByIsoCode(String oCode);
}
