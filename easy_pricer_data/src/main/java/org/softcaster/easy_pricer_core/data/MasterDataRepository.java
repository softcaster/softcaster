package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {

    public MasterData findByIdMasterData(Integer idMasterData);
}
