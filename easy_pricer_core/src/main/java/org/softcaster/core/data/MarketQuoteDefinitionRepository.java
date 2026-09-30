package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarketQuoteDefinitionRepository extends JpaRepository<MarketQuoteDefinition, Integer> {

    @Query("""
        SELECT mqd
        FROM MarketQuoteDefinition mqd
        WHERE mqd.marketQuoteDefinitionId = :id
        """)
    @EntityGraph("MarketQuoteDefinition.fullWithCountry")
    public MarketQuoteDefinition findByIdWithCountry(@Param("id") Integer id);

    @EntityGraph("MarketQuoteDefinition.fullWithCountry")
    @Override
    public List<MarketQuoteDefinition> findAll(Sort sortByCode);

    @Query("""
        SELECT mqd
        FROM MarketQuoteDefinition mqd
        WHERE mqd.code = :code
        """)
    @EntityGraph("MarketQuoteDefinition.fullWithCountry")
    public MarketQuoteDefinition findByCodeWithCountry(@Param("code") String code);

    @Query("""
        SELECT mqd
        FROM MarketQuoteDefinition mqd
        WHERE mqd.code = :code
        """)
    @EntityGraph("MarketQuoteDefinition.fullWithQuotes")
    public MarketQuoteDefinition findByCodeWithQuotes(@Param("code") String code);
}
