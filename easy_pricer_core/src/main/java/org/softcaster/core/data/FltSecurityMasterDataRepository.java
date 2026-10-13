package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FltSecurityMasterDataRepository extends BaseMasterDataRepository<FltSecurityMasterData> {

    @Query("""
        SELECT md
        FROM FltSecurityMasterData md
        WHERE md.idMasterData = :id
        """)
    @EntityGraph("FltSecurityMasterData.indexGraph")
    Optional<FltSecurityMasterData> findByIdWithRefRateIndex(@Param("id") Integer id);
    
        @Query("""
        SELECT fsmd
        FROM FltSecurityMasterData fsmd
        WHERE fsmd.code = :code
        """)
    @EntityGraph("SecurityMasterData.fullGraphWithCashFlow")
    FltSecurityMasterData findByCodeWithCashFlow(@Param("code") String code);

}
