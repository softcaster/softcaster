package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface MmFutureMasterDataRepository extends BaseMasterDataRepository<MmFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<MmFutureMasterData> findAll(Sort sort);}
