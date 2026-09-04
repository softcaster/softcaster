package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionMasterDataRepository extends JpaRepository<PositionMasterData, Integer> {

    public PositionMasterData findByIdPosition(Integer idPosition);

    public PositionMasterData findByCode(String code);

    @EntityGraph(attributePaths = {"currency", "portfolio"})
    @Override
    public List<PositionMasterData> findAll(Sort sort);
}
