package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BondFutureMasterDataRepository extends BaseMasterDataRepository<BondFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<BondFutureMasterData> findAll(Sort sort);

    /**
     * Scadenza con testata, calendario e paniere.
     */
    @Query("""
        SELECT bfmd
        FROM BondFutureMasterData bfmd
        WHERE bfmd.isin = :isin
        """)
    @EntityGraph("BondFutureMasterData.fullGraph")
    public Optional<BondFutureMasterData> findByIsin(@Param("isin") String in);

    /**
     * Come findByIsin, cercando per codice (MBTPZ6).
     */
    @Query("""
        SELECT bfmd
        FROM BondFutureMasterData bfmd
        WHERE bfmd.code = :code
        """)
    @EntityGraph("BondFutureMasterData.fullGraph")
    public Optional<BondFutureMasterData> findByCodeWithDeliverables(@Param("code") String code);

    /**
     * Scadenza della testata per mese: al massimo una.
     */
    @Query("""
        SELECT bfmd
        FROM BondFutureMasterData bfmd
        WHERE bfmd.product.idFutureProduct = :idProduct
          AND bfmd.contractMonth = :contractMonth
        """)
    Optional<BondFutureMasterData> findByProductAndContractMonth(@Param("idProduct") Integer idProduct,
            @Param("contractMonth") java.sql.Date contractMonth);
}
