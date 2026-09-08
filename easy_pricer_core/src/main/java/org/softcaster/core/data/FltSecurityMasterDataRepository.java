package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FltSecurityMasterDataRepository extends JpaRepository<FltSecurityMasterData, Integer> {

    public FltSecurityMasterData findByIdMasterData(Integer idMasterData);

    public FltSecurityMasterData findByIsin(String isin);
    
    @EntityGraph(attributePaths = {"issuer", "currency", "refRateIndex", "cashFlows"})
    @Override
    public List<FltSecurityMasterData> findAll(Sort sort);
}
