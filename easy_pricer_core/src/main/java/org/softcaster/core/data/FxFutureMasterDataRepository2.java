package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface FxFutureMasterDataRepository2 extends BaseMasterDataRepository<FxFutureMasterData2>  {

    @EntityGraph("FxFutureMasterData.fullGraph")
    @Override
    List<FxFutureMasterData2> findAll(Sort sort);

}
