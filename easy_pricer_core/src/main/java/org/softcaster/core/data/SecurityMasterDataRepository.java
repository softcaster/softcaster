package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecurityMasterDataRepository extends BaseMasterDataRepository<SecurityMasterData> {

    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.isin = :isin
        """)
    @EntityGraph("MasterData.fullGraph")
    public Optional<SecurityMasterData> findByIsin(@Param("isin") String isin);

    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.currency.isoCode = :code
        """)
    @EntityGraph("MasterData.fullGraph")
    public Optional<List<SecurityMasterData>> findByCurrency(@Param("code") String currencyCode);
    
    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.idMasterData = :id
        """)
    @EntityGraph("SecurityMasterData.withCashFlow")
    public Optional<SecurityMasterData> findByIdWithCashFlow(@Param("id") Integer id);
}
