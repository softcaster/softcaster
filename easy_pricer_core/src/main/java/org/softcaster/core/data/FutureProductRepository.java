package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Accesso alle testate dei future (tutte le sottoclassi: query polimorfiche).
 */
public interface FutureProductRepository extends JpaRepository<FutureProduct, Integer> {

    @EntityGraph(attributePaths = {"currency", "assetClass"})
    Optional<FutureProduct> findByCode(String code);

    @Query("SELECT p FROM FutureProduct p ORDER BY p.code")
    @EntityGraph(attributePaths = {"currency", "assetClass"})
    List<FutureProduct> findAllOrderByCode();
}
