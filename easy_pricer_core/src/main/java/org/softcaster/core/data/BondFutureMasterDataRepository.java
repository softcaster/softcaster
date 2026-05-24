package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BondFutureMasterDataRepository extends JpaRepository<BondFutureMasterData, Integer> {

    public BondFutureMasterData findByIdMasterData(Integer idMasterData);

    public BondFutureMasterData findByIsin(String isin);

    @Query("SELECT bfut FROM BondFutureMasterData bfut WHERE bfut.assetClass.code = :code ORDER BY bfut.maturityDate ASC")
    public List<BondFutureMasterData> findAllByAssetClass(String code);
}
