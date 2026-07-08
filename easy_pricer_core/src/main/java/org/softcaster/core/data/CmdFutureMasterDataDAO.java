package org.softcaster.core.data;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cmdFutureMasterDataDAO")
public class CmdFutureMasterDataDAO {

    private final CmdFutureMasterDataRepository repository;

    public CmdFutureMasterDataDAO(CmdFutureMasterDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CmdFutureMasterData> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public CmdFutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public CmdFutureMasterData saveOrUpdate(CmdFutureMasterData cmdFutureMasterData) {
        return repository.save(cmdFutureMasterData);
    }

    @Transactional
    public void delete(CmdFutureMasterData cmdFutureMasterData) {
        repository.delete(cmdFutureMasterData);
    }

}
