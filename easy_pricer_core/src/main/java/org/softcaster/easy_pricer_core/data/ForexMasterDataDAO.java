package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("forexMasterDataDAO")
public class ForexMasterDataDAO {

    @Resource
    private ForexMasterDataRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public ForexMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional(readOnly = true)
    public ForexMasterData findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional
    public ForexMasterData saveOrUpdate(ForexMasterData forexMasterData) {
        return repository.save(forexMasterData);
    }

    @Transactional
    public void delete(ForexMasterData forexMasterData) {
        repository.delete(forexMasterData);
    }
    
    @Transactional(readOnly = true)
    public List<ForexMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

}
