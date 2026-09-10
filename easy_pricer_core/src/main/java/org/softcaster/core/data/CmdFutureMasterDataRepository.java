package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CmdFutureMasterDataRepository extends BaseMasterDataRepository<CmdFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<CmdFutureMasterData> findAll(Sort sort);

}
