package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionMasterDataRepository extends JpaRepository<PositionMasterData, Integer> {

    public PositionMasterData findByIdPosition(Integer idPosition);

    public PositionMasterData findByCode(String code);

    @EntityGraph(attributePaths = {"currency", "parent"})
    @Override
    public List<PositionMasterData> findAll(Sort sort);

    @EntityGraph(attributePaths = {"currency", "parent"})
    public List<PositionMasterData> findByPostable(boolean postable, Sort sort);

    public boolean existsByParentIdPosition(Integer idPosition);

    @Query("select count(d) > 0 from PositionDetail d where d.positionMd = :idPosition")
    public boolean hasDetails(@Param("idPosition") Integer idPosition);
}
