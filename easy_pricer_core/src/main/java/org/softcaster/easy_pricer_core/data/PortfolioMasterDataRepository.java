package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioMasterDataRepository extends JpaRepository<PortfolioMasterData, Integer> {

    public PortfolioMasterData findByIdPortfolio(Integer idPortfolio);
}
