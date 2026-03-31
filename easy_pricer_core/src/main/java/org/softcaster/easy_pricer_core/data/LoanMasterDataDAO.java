package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("loanMasterDataDAO")
public class LoanMasterDataDAO {

    @Resource
    private LoanMasterDataRepository repository;

    private final Sort sortByMaturity = Sort.by(Sort.Direction.ASC, "maturityDate");

    @Transactional(readOnly = true)
    public LoanMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public LoanMasterData saveOrUpdate(LoanMasterData loanMasterData) {
        return repository.save(loanMasterData);
    }

    @Transactional
    public void delete(LoanMasterData loanMasterData) {
        repository.delete(loanMasterData);
    }

    @Transactional(readOnly = true)
    public List<LoanMasterData> findAll() {
        return repository.findAll(sortByMaturity);
    }

}
