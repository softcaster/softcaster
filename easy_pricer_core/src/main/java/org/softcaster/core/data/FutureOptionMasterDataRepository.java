package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FutureOptionMasterDataRepository extends BaseMasterDataRepository<FutureOptionMasterData> {

    /**
     * Lista per le finestre di anagrafica: testata e sottostante, letti fuori
     * dalla transazione.
     */
    @EntityGraph(value = "FutureOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<FutureOptionMasterData> findAll(Sort sort);

    @Query("""
        SELECT omd
        FROM FutureOptionMasterData omd
        WHERE omd.product.assetClass.code IN :assetClasses
        ORDER BY omd.product.code, omd.maturityDate, omd.optionType, omd.strike
        """)
    @EntityGraph(value = "FutureOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    List<FutureOptionMasterData> findAllByProductAssetClass(@Param("assetClasses") List<String> assetClasses);

    @Query("""
        SELECT omd
        FROM FutureOptionMasterData omd
        WHERE omd.code = :code
        """)
    @EntityGraph(value = "FutureOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FutureOptionMasterData> findByCodeWithUnderlying(@Param("code") String code);

    @Query("""
        SELECT omd
        FROM FutureOptionMasterData omd
        WHERE omd.isin = :isin
        """)
    @EntityGraph(value = "FutureOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FutureOptionMasterData> findByIsinWithUnderlying(@Param("isin") String isin);
}