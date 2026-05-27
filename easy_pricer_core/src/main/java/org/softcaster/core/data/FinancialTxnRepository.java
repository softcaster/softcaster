package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialTxnRepository extends JpaRepository<FinancialTxn, Integer> {

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

    public List<FinancialTxn> findByTxnStatusCode(String code);
}
