package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface IndexMasterDataRepository extends BaseMasterDataRepository<IndexMasterData> {

    @EntityGraph(value = "MasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<IndexMasterData> findAll(Sort sort);
}
