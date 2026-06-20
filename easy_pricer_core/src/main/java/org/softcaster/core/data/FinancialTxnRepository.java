package org.softcaster.core.data;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface FinancialTxnRepository extends JpaRepository<FinancialTxn, Integer> {

    // FORZA IL FETCH: Carica la transazione, lo strumento e l'asset class in una sola JOIN
    // @EntityGraph(attributePaths = {"masterData", "masterData.assetClass"})    
    public FinancialTxn findByIdFinancialTxn(Integer idFinancialTxn);

    // Il nome riflette il percorso: MasterData -> Daycount -> Code
    // List<FinancialTxn> findAllByMasterData_Daycount_Code(String code);    
    // Ottimizzato con JOIN FETCH: carica la transazione e il MasterData in un solo colpo
    @Query("SELECT f FROM FinancialTxn f "
            + "JOIN FETCH f.masterData m "
            + "WHERE m.daycount = :daycount")
    public List<FinancialTxn> findAllByDaycount(@Param("daycount") DaycountBasis daycount);

    // Ottimizzato con JOIN FETCH
    @Query("SELECT f FROM FinancialTxn f "
            + "JOIN FETCH f.masterData m "
            + "WHERE m.assetClass.code = :code")
    public List<FinancialTxn> findAllByAssetClass(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // Attiva lo SKIP LOCKED su Postgres
    @Query("SELECT t FROM FinancialTxn t WHERE t.txnStatus = :status ORDER BY t.idFinancialTxn ASC")
    public List<FinancialTxn> getAndLockByStatusCode(@Param("status") TxnStatus status/*, Pageable pageable*/);

    @Query("""
        select t
        from FinancialTxn t
        join fetch t.masterData md
        join fetch md.assetClass ac
        where t.idFinancialTxn = :id
    """)
    public FinancialTxn findByIdWithMasterData(@Param("id") Integer idFinancialTxn);
}
