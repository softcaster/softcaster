package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FxFutureMasterDataRepository extends BaseMasterDataRepository<FxFutureMasterData> {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<FxFutureMasterData> findAll(Sort sort);

    @Query("""
        SELECT ffmd
        FROM FxFutureMasterData ffmd
        WHERE ffmd.code = :code
        """)
    @EntityGraph("FxFutureMasterData.fullGraph")
    Optional<FxFutureMasterData> findByCodeWithUnderlying(@Param("code") String code);
}
