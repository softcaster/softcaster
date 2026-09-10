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

    @Query("""
        SELECT bfmd
        FROM BondFutureMasterData bfmd
        WHERE bfmd.isin = :isin
        """)
    @EntityGraph("MasterData.valuationGraph")
    public Optional<BondFutureMasterData> findByIsin(@Param("isin") String in);
}
