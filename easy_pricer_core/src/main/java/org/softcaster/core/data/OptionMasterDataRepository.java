package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionMasterDataRepository extends BaseMasterDataRepository<OptionMasterData> {

    /**
     * Lista per le finestre di anagrafica: testata e sottostante, letti fuori
     * dalla transazione.
     */
    @EntityGraph(value = "OptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<OptionMasterData> findAll(Sort sort);

    @Query("""
        SELECT omd
        FROM OptionMasterData omd
        WHERE omd.product.assetClass.code IN :assetClasses
        ORDER BY omd.product.code, omd.maturityDate, omd.optionType, omd.strike
        """)
    @EntityGraph(value = "OptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    List<OptionMasterData> findAllByProductAssetClass(@Param("assetClasses") List<String> assetClasses);

    @Query("""
        SELECT omd
        FROM OptionMasterData omd
        WHERE omd.code = :code
        """)
    @EntityGraph(value = "OptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OptionMasterData> findByCodeWithUnderlying(@Param("code") String code);

    @Query("""
        SELECT omd
        FROM OptionMasterData omd
        WHERE omd.isin = :isin
        """)
    @EntityGraph(value = "OptionMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OptionMasterData> findByIsinWithUnderlying(@Param("isin") String isin);
}
