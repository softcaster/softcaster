package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 *
 * @author ep
 */
public interface ForexMasterDataRepository2 extends BaseMasterDataRepository<ForexMasterData2>  {

    @EntityGraph("ForexMasterData.currenciesGraph")
    @Override
    List<ForexMasterData2> findAll(Sort sort);

}
