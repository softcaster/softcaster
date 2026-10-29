package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccumulatorMasterDataRepository extends BaseMasterDataRepository<AccumulatorMasterData> {

    @Query("""
        SELECT amd
        FROM AccumulatorMasterData amd
        WHERE amd.code = :code
        """)
    @EntityGraph(value = "AccumulatorMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<AccumulatorMasterData> findByCodeWithFixings(@Param("code") String code);

    @Query("""
        SELECT amd
        FROM AccumulatorMasterData amd
        WHERE amd.idMasterData = :id
        """)
    @EntityGraph(value = "AccumulatorMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<AccumulatorMasterData> findByIdWithFixings(@Param("id") Integer id);

    /**
     * Contratti delle asset class indicate (FXA, CMA), per la lista del client.
     */
    @Query("""
        SELECT amd
        FROM AccumulatorMasterData amd
        WHERE amd.assetClass.code IN :assetClasses
        ORDER BY amd.issueDate DESC, amd.idMasterData DESC
        """)
    @EntityGraph(value = "AccumulatorMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    List<AccumulatorMasterData> findAllByAssetClasses(@Param("assetClasses") List<String> assetClasses);
}
