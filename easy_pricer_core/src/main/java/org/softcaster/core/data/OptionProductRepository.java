package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OptionProductRepository extends JpaRepository<OptionProduct, Integer> {

    @EntityGraph(attributePaths = {"currency", "assetClass"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<OptionProduct> findByCode(String code);

    @Query("SELECT p FROM OptionProduct p ORDER BY p.code")
    @EntityGraph(attributePaths = {"currency", "assetClass"}, type = EntityGraph.EntityGraphType.LOAD)
    List<OptionProduct> findAllOrderByCode();
}
