package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmdFutureMasterDataRepository extends JpaRepository<CmdFutureMasterData, Integer> {

    public CmdFutureMasterData findByIdMasterData(Integer idMasterData);
    
    @EntityGraph(attributePaths = {"currency"})
    @Override
    public List<CmdFutureMasterData> findAll(Sort sort);
}
