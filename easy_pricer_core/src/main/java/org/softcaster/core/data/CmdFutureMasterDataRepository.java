package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CmdFutureMasterDataRepository extends JpaRepository<CmdFutureMasterData, Integer> {

    public CmdFutureMasterData findByIdMasterData(Integer idMasterData);
}
