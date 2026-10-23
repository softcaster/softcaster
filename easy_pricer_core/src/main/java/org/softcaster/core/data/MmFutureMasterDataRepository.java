package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

public interface MmFutureMasterDataRepository extends BaseMasterDataRepository<MmFutureMasterData>  {

    /**
     * Lista per le finestre di anagrafica: con testata e sottostante, usati
     * fuori dalla transazione (combo del sottostante).
     */
    @EntityGraph(attributePaths = {"instrumentValuation", "currency", "assetClass", "product", "product.underlying"},
            type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<MmFutureMasterData> findAll(Sort sort);
}
