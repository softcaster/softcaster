package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface PowerFutureMasterDataRepository extends BaseMasterDataRepository<PowerFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<PowerFutureMasterData> findAll(Sort sort);

}
