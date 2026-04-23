package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionMasterDataRepository extends JpaRepository<PositionMasterData, Integer> {

    public PositionMasterData findByIdPosition(Integer idPosition);

    public PositionMasterData findByCode(String code);
}
