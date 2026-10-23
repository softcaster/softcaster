package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecurityMasterDataRepository extends BaseMasterDataRepository<SecurityMasterData> {

    /**
     * Lista per le finestre di anagrafica: emittente e flussi, letti fuori
     * dalla transazione.
     */
    @EntityGraph(value = "SecurityMasterData.fullGraphWithCashFlow", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<SecurityMasterData> findAll(Sort sort);

    /**
     * Come in BaseMasterDataRepository, con emittente e flussi (finestre di
     * anagrafica).
     */
    @Query("""
        SELECT md
        FROM SecurityMasterData md
        WHERE md.assetClass.code = :code
        ORDER BY md.maturityDate ASC
        """)
    @EntityGraph(value = "SecurityMasterData.fullGraphWithCashFlow", type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<SecurityMasterData> findAllByAssetClass(@Param("code") String code);

    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.isin = :isin
        """)
    @EntityGraph("SecurityMasterData.fullGraphWithCashFlow")
    public Optional<SecurityMasterData> findByIsin(@Param("isin") String isin);
    
    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.idMasterData = :id
        """)
    @EntityGraph("SecurityMasterData.fullGraphWithCashFlow")
    public Optional<SecurityMasterData> findByIdWithIssuer(@Param("id") Integer id);

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

    @Query("""
        SELECT smd
        FROM SecurityMasterData smd
        WHERE smd.code = :code
        """)
    @EntityGraph("SecurityMasterData.withCashFlow")
    public Optional<SecurityMasterData> findByCodeWithCashFlow(@Param("code") String code);
}
