package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface BondFutureMasterDataRepository2 extends BaseMasterDataRepository<BondFutureMasterData2>  {

    @EntityGraph("BondFutureMasterData.fullGraph")
    @Override
    List<BondFutureMasterData2> findAll(Sort sort);
}
