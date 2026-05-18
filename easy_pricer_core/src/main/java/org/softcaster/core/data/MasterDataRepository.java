package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {

    public MasterData findByIdMasterData(Integer idMasterData);

    public MasterData findByCode(String code);
}
