package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexMasterDataRepository extends JpaRepository<ForexMasterData, Integer> {

    public ForexMasterData findByIdMasterData(Integer idMasterData);

    public ForexMasterData findByCode(String code);
}
