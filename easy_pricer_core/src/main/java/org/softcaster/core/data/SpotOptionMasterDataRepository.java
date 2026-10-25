package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpotOptionMasterDataRepository extends BaseMasterDataRepository<SpotOptionMasterData> {

    /**
     * Lista per le finestre di anagrafica: testata e sottostante, letti fuori
     * dalla transazione.
     * @param sort
     * @return 
     */
    @EntityGraph(value = "SpotOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<SpotOptionMasterData> findAll(Sort sort);

    @Query("""
        SELECT omd
        FROM SpotOptionMasterData omd
        WHERE omd.product.assetClass.code IN :assetClasses
        ORDER BY omd.product.code, omd.maturityDate, omd.optionType, omd.strike
        """)
    @EntityGraph(value = "SpotOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    List<SpotOptionMasterData> findAllByProductAssetClass(@Param("assetClasses") List<String> assetClasses);

    @Query("""
        SELECT omd
        FROM SpotOptionMasterData omd
        WHERE omd.code = :code
        """)
    @EntityGraph(value = "SpotOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<SpotOptionMasterData> findByCodeWithUnderlying(@Param("code") String code);

    @Query("""
        SELECT omd
        FROM SpotOptionMasterData omd
        WHERE omd.isin = :isin
        """)
    @EntityGraph(value = "SpotOptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<SpotOptionMasterData> findByIsinWithUnderlying(@Param("isin") String isin);
}