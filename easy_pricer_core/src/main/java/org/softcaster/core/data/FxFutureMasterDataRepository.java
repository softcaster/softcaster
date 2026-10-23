package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FxFutureMasterDataRepository extends BaseMasterDataRepository<FxFutureMasterData> {

    /**
     * Lista per le finestre di anagrafica: con testata e coppia sottostante,
     * usate fuori dalla transazione (combo del sottostante).
     */
    @EntityGraph(value = "FxFutureMasterData.fullGraph", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<FxFutureMasterData> findAll(Sort sort);

    /**
     * Scadenza con testata, coppia sottostante e calendari delle due valute.
     */
    @Query("""
        SELECT ffmd
        FROM FxFutureMasterData ffmd
        WHERE ffmd.code = :code
        """)
    @EntityGraph("FxFutureMasterData.fullGraph")
    Optional<FxFutureMasterData> findByCodeWithUnderlying(@Param("code") String code);

    /**
     * Come findByCodeWithUnderlying, cercando per ISIN.
     */
    @Query("""
        SELECT ffmd
        FROM FxFutureMasterData ffmd
        WHERE ffmd.isin = :isin
        """)
    @EntityGraph("FxFutureMasterData.fullGraph")
    Optional<FxFutureMasterData> findByIsinWithUnderlying(@Param("isin") String isin);
}
