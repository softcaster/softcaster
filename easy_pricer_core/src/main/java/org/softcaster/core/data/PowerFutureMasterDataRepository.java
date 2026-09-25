package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PowerFutureMasterDataRepository extends BaseMasterDataRepository<PowerFutureMasterData>  {

    @EntityGraph("MasterData.fullGraph")
    @Override
    List<PowerFutureMasterData> findAll(Sort sort);

    @Query("""
        SELECT pfmd
        FROM PowerFutureMasterData pfmd
        WHERE pfmd.idMasterData = :id
        """)
    @EntityGraph("PowerFutureMasterData.fullGraph")
    PowerFutureMasterData findByIdWithDeliveryProfile(@Param("id") Integer idMasterData);
}
