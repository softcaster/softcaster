package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accesso alle testate dei future (tutte le sottoclassi: query polimorfiche).
 */
public interface FutureProductRepository extends JpaRepository<FutureProduct, Integer> {

    @EntityGraph(attributePaths = {"currency", "assetClass"})
    Optional<FutureProduct> findByCode(String code);

    @Query("SELECT p FROM FutureProduct p ORDER BY p.code")
    @EntityGraph(attributePaths = {"currency", "assetClass"})
    List<FutureProduct> findAllOrderByCode();

    /**
     * Scadenze e opzioni che puntano alla testata (le tabelle delle scadenze
     * hanno la colonna future_product nelle sottoclassi: query nativa).
     */
    @Query(nativeQuery = true, value = """
            SELECT (SELECT count(*) FROM bond_future_master_data WHERE future_product = :id)
                 + (SELECT count(*) FROM fx_future_master_data WHERE future_product = :id)
                 + (SELECT count(*) FROM mm_future_master_data WHERE future_product = :id)
                 + (SELECT count(*) FROM cmd_future_master_data WHERE future_product = :id)
                 + (SELECT count(*) FROM future_option_product WHERE future_product = :id)
            """)
    long countReferences(@Param("id") Integer idFutureProduct);
}
