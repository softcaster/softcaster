package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 *
 * @author ep
 */
public interface ForexMasterDataRepository extends BaseMasterDataRepository<ForexMasterData>  {

    @EntityGraph("ForexMasterData.currenciesGraph")
    @Override
    List<ForexMasterData> findAll(Sort sort);

}
