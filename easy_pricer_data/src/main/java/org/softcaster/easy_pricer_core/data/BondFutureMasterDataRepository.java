package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BondFutureMasterDataRepository extends JpaRepository<BondFutureMasterData, Integer> {

    public BondFutureMasterData findByIdMasterData(Integer idMasterData);

    public BondFutureMasterData findByIsin(String isin);
}
