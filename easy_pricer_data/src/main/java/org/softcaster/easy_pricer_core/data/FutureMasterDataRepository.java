package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FutureMasterDataRepository extends JpaRepository<FutureMasterData, Integer> {

    public FutureMasterData findByIdMasterData(Integer idMasterData);
}
