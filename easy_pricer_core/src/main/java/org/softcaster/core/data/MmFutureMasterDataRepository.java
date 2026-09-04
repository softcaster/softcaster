package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MmFutureMasterDataRepository extends JpaRepository<MmFutureMasterData, Integer> {

    public MmFutureMasterData findByIdMasterData(Integer idMasterData);

    public MmFutureMasterData findByIsin(String isin);
    
    @EntityGraph(attributePaths = {"currency"})
    @Override
    public List<MmFutureMasterData> findAll(Sort sort);
}
