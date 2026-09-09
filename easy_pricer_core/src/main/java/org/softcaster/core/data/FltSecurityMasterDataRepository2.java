package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FltSecurityMasterDataRepository2 extends BaseMasterDataRepository<FltSecurityMasterData2> {

    @Query("""
        SELECT md
        FROM FltSecurityMasterData2 md
        WHERE md.idMasterData = :id
        """)
    @EntityGraph("FltSecurityMasterData.indexGraph")
    Optional<FltSecurityMasterData2> findByIdWithRefRateIndex(@Param("id") Integer id);
}
