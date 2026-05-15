package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FxFutureMasterDataRepository extends JpaRepository<FxFutureMasterData, Integer> {

    public FxFutureMasterData findByIdMasterData(Integer idMasterData);

    public FxFutureMasterData findByIsin(String isin);
}
