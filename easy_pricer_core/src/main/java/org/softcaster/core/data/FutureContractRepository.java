package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accesso alle scadenze di tutti i future (listed_contract con contract_type
 * 'FUTURE' o 'BOND_FUTURE'): le query sono polimorfiche.
 */
public interface FutureContractRepository extends JpaRepository<FutureContract, Integer> {

    /**
     * Contratto con testata e calendario.
     */
    @EntityGraph("FutureContract.withProduct")
    Optional<FutureContract> findByCode(String code);

    /**
     * Come findByCode, cercando per ISIN del contratto.
     */
    @EntityGraph("FutureContract.withProduct")
    Optional<FutureContract> findByIsin(String isin);

    /**
     * Chiave naturale: testata + mese di scadenza.
     */
    @Query("""
        SELECT fc
        FROM FutureContract fc
        WHERE fc.masterData.idMasterData = :idMasterData
          AND fc.contractMonth = :contractMonth
        """)
    @EntityGraph("FutureContract.withProduct")
    Optional<FutureContract> findByMasterDataAndContractMonth(@Param("idMasterData") Integer idMasterData,
            @Param("contractMonth") java.sql.Date contractMonth);

    /**
     * Scadenze di una testata in ordine di ultimo giorno di negoziazione.
     */
    @Query("""
        SELECT fc
        FROM FutureContract fc
        WHERE fc.masterData.idMasterData = :idMasterData
        ORDER BY fc.lastTradingDate
        """)
    List<FutureContract> findByMasterData(@Param("idMasterData") Integer idMasterData);
}
