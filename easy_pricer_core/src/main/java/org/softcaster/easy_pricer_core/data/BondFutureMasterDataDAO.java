package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bondFutureMasterDataDAO")
public class BondFutureMasterDataDAO {

    @Resource
    private BondFutureMasterDataRepository repository;

    @Transactional(readOnly = true)
    public BondFutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public BondFutureMasterData saveOrUpdate(BondFutureMasterData bondFutureMasterData) {
        return repository.save(bondFutureMasterData);
    }

    @Transactional
    public void delete(BondFutureMasterData bondFutureMasterData) {
        repository.delete(bondFutureMasterData);
    }

    @Transactional(readOnly = true)
    public List<BondFutureMasterData> findAll() {
        return repository.findAll();
    }

    public BondFutureMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }
}
