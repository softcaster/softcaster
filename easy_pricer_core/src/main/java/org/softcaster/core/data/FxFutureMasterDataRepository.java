package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface FxFutureMasterDataRepository extends BaseMasterDataRepository<FxFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<FxFutureMasterData> findAll(Sort sort);

}
