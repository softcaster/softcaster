package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioMasterDataRepository extends JpaRepository<PortfolioMasterData, Integer> {

    public PortfolioMasterData findByIdPortfolio(Integer idPortfolio);
    
    @EntityGraph(attributePaths = {"currency"})
    @Override
    public List<PortfolioMasterData> findAll(Sort sort);
}
