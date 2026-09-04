package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    public Currency findByIdCurrency(Integer idCurrency);

    public Currency findByIsoCode(String oCode);

    @EntityGraph(attributePaths = {"calendar"})
    @Override
    public List<Currency> findAll(Sort sort);
}
