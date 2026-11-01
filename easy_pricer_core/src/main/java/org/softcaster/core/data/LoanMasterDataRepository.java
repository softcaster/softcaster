package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanMasterDataRepository extends BaseMasterDataRepository<LoanMasterData> {

    /**
     * Mutuo con valuta (e calendario), asset class e piano delle rate; le
     * spese si caricano in transazione.
     */
    @Query("""
        SELECT lmd
        FROM LoanMasterData lmd
        WHERE lmd.code = :code
        """)
    @EntityGraph(value = "LoanMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<LoanMasterData> findByCodeWithCashFlows(@Param("code") String code);

    @Query("""
        SELECT lmd
        FROM LoanMasterData lmd
        WHERE lmd.idMasterData = :id
        """)
    @EntityGraph(value = "LoanMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<LoanMasterData> findByIdWithCashFlows(@Param("id") Integer id);

    @Query("""
        SELECT lmd
        FROM LoanMasterData lmd
        ORDER BY lmd.issueDate DESC, lmd.idMasterData DESC
        """)
    @EntityGraph(value = "LoanMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    List<LoanMasterData> findAllWithCashFlows();
}
