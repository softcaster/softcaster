package org.softcaster.core.data;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fltSecurityMasterDataDAO")
public class FltSecurityMasterDataDAO {

    private final FltSecurityMasterDataRepository repository;

    // Iniezione tramite costruttore (Best Practice per Spring)
    public FltSecurityMasterDataDAO(FltSecurityMasterDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FltSecurityMasterData> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public FltSecurityMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public FltSecurityMasterData saveOrUpdate(FltSecurityMasterData fltSecurityMasterData) {
        return repository.save(fltSecurityMasterData);
    }

    @Transactional
    public void delete(FltSecurityMasterData fltSecurityMasterData) {
        repository.delete(fltSecurityMasterData);
    }

}
