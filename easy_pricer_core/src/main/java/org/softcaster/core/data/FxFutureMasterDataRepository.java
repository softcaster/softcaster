package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FxFutureMasterDataRepository extends JpaRepository<FxFutureMasterData, Integer> {

    public FxFutureMasterData findByIdMasterData(Integer idMasterData);

    public FxFutureMasterData findByIsin(String isin);
    
    @EntityGraph(attributePaths = {"currency"})
    @Override
    public List<FxFutureMasterData> findAll(Sort sort);
}
