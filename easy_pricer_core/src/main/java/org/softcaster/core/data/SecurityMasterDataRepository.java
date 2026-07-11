package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecurityMasterDataRepository extends JpaRepository<SecurityMasterData, Integer> {

    public SecurityMasterData findByIdMasterData(Integer idMasterData);

    public SecurityMasterData findByIsin(String isin);

    public List<SecurityMasterData> findByCurrencyIsoCode(String currencyCode);

    List<SecurityMasterData> findByDescriptionContaining(String issueDescriptionFragment);

    @Query("SELECT smd FROM SecurityMasterData smd WHERE smd.assetClass.code = :code ORDER BY smd.maturityDate ASC")
    public List<SecurityMasterData> findAllByAssetClass(String code);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CashFlowItem c WHERE c.masterData = :idMasterData")
    void deleteCashFlowsByMasterDataId(@Param("idMasterData") Integer idMasterData);
}
