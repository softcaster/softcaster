package org.softcaster.core.data;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
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
            + "WHERE m.daycount.code = :code")
    public List<FinancialTxn> findAllByDaycountCode(@Param("code") String code);

    // Ottimizzato con JOIN FETCH
    @Query("SELECT f FROM FinancialTxn f "
            + "JOIN FETCH f.masterData m "
            + "WHERE m.assetClass.code = :code")
    public List<FinancialTxn> findAllByAssetClass(@Param("code") String code);

    @Query(value = "SELECT f.* FROM financial_txn f " +
                   "JOIN txn_status s ON f.txn_status = s.id_txn_status " +
                   "WHERE s.code = :code " +
                   "FOR UPDATE SKIP LOCKED", nativeQuery = true)
    public List<FinancialTxn> findByTxnStatusCode(String code);

    @Query("""
        select t
        from FinancialTxn t
        join fetch t.masterData md
        join fetch md.assetClass ac
        where t.idFinancialTxn = :id
    """)
    public FinancialTxn findByIdWithMasterData( @Param("id") Integer idFinancialTxn);
}
