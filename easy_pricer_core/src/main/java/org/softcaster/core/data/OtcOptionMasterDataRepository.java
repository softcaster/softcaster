package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Opzioni OTC, entrambe le foglie (query polimorfiche sulla classe astratta).
 * Il sottostante e' LAZY: si legge nella transazione del chiamante.
 */
public interface OtcOptionMasterDataRepository extends BaseMasterDataRepository<OtcOptionMasterData> {

    @Query("""
        SELECT o
        FROM OtcOptionMasterData o
        JOIN FETCH o.currency
        JOIN FETCH o.assetClass
        WHERE o.code = :code
        """)
    Optional<OtcOptionMasterData> findOtcByCode(@Param("code") String code);

    @Query("""
        SELECT o
        FROM OtcOptionMasterData o
        JOIN FETCH o.currency
        JOIN FETCH o.assetClass
        WHERE o.idMasterData = :id
        """)
    Optional<OtcOptionMasterData> findOtcById(@Param("id") Integer id);

    @Query("""
        SELECT o
        FROM OtcOptionMasterData o
        JOIN FETCH o.currency
        JOIN FETCH o.assetClass
        WHERE o.assetClass.code IN :assetClasses
        ORDER BY o.issueDate DESC, o.idMasterData DESC
        """)
    List<OtcOptionMasterData> findAllByAssetClasses(@Param("assetClasses") List<String> assetClasses);
}
