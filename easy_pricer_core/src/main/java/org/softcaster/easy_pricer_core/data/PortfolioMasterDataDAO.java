package org.softcaster.easy_pricer_core.data;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("portfolioMasterDataDAO")
public class PortfolioMasterDataDAO {

    @Resource
    private PortfolioMasterDataRepository repository;
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public PortfolioMasterData findByIdPortfolio(Integer idPortfolio) {
        return repository.findByIdPortfolio(idPortfolio);
    }
    
    @Transactional(readOnly = true)
    public List<PortfolioMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public PortfolioMasterData saveOrUpdate(PortfolioMasterData portfolioMasterData) {
        return repository.save(portfolioMasterData);
    }

    @Transactional
    public void delete(PortfolioMasterData portfolioMasterData) {
        repository.delete(portfolioMasterData);
    }

}
