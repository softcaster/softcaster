package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FltSecurityMasterDataRepository extends JpaRepository<FltSecurityMasterData, Integer> {

    public FltSecurityMasterData findByIdMasterData(Integer idMasterData);
}
