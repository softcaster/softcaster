package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FltSecurityMasterDataRepository extends BaseMasterDataRepository<FltSecurityMasterData> {

    /**
     * Lista per le finestre di anagrafica: emittente, indice di riferimento e
     * flussi, letti fuori dalla transazione.
     */
    @EntityGraph(value = "FltSecurityMasterData.fullGraphWithCashFlow", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<FltSecurityMasterData> findAll(Sort sort);

    @Query("""
        SELECT md
        FROM FltSecurityMasterData md
        WHERE md.idMasterData = :id
        """)
    @EntityGraph("FltSecurityMasterData.indexGraph")
    Optional<FltSecurityMasterData> findByIdWithRefRateIndex(@Param("id") Integer id);
    
        @Query("""
        SELECT fsmd
        FROM FltSecurityMasterData fsmd
        WHERE fsmd.code = :code
        """)
    @EntityGraph("SecurityMasterData.fullGraphWithCashFlow")
    FltSecurityMasterData findByCodeWithCashFlow(@Param("code") String code);

}
