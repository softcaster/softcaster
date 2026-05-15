package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyPairRepository extends JpaRepository<CurrencyPair, Integer> {

    public CurrencyPair findByIdCurrencyPair(Integer idCurrencyPair);
}
