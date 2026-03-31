package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("masterDataDAO")
public class MasterDataDAO {

    @Resource
    private MasterDataRepository repository;

    @Transactional(readOnly = true)
    public MasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public MasterData saveOrUpdate(MasterData masterData) {
        return repository.save(masterData);
    }

    @Transactional
    public void delete(MasterData masterData) {
        repository.delete(masterData);
    }

    @Transactional(readOnly = true)
    public List<MasterData> findAll() {
        return repository.findAll();
    }

}
