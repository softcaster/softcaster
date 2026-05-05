package org.softcaster.easy_pricer_core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialTxnRepository extends JpaRepository<FinancialTxn, Integer> {

    public FinancialTxn findByIdFinancialTxn(Integer idFinancialTxn);
    
    // Il nome riflette il percorso: MasterData -> Daycount -> Code
    // List<FinancialTxn> findAllByMasterData_Daycount_Code(String code);    
    
    // Query JPQL esplicita
    @Query("SELECT f FROM FinancialTxn f WHERE f.masterData.daycount.code = :code")
    public List<FinancialTxn> findAllByDaycountCode(@Param("code") String code);

    @Query("SELECT f FROM FinancialTxn f WHERE f.masterData.assetClass.code = :code")
    public List<FinancialTxn> findAllByAssetClass(@Param("code") String code);

    public List<FinancialTxn> findByTxnStatusCode(String code);
}
