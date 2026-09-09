package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface MmFutureMasterDataRepository2 extends BaseMasterDataRepository<MmFutureMasterData2>  {

    @EntityGraph("MmFutureMasterData.fullGraph")
    @Override
    List<MmFutureMasterData2> findAll(Sort sort);}
