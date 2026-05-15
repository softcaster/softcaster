package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("futureMasterDataDAO")
public class FutureMasterDataDAO {

    @Resource
    private FutureMasterDataRepository repository;

    @Transactional(readOnly = true)
    public FutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public FutureMasterData saveOrUpdate(FutureMasterData futureMasterData) {
        return repository.save(futureMasterData);
    }

    @Transactional
    public void delete(FutureMasterData futureMasterData) {
        repository.delete(futureMasterData);
    }

    @Transactional(readOnly = true)
    public List<FutureMasterData> findAll() {
        return repository.findAll();
    }
}
