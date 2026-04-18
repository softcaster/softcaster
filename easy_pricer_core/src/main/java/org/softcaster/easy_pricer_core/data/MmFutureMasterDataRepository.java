package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MmFutureMasterDataRepository extends JpaRepository<MmFutureMasterData, Integer> {

    public MmFutureMasterData findByIdMasterData(Integer idMasterData);

    public MmFutureMasterData findByIsin(String isin);
}
