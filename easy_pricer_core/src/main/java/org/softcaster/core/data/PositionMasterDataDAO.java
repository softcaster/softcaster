package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("positionMasterDataDAO")
public class PositionMasterDataDAO {

    @Resource
    private PositionMasterDataRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");
    
    @Transactional(readOnly = true)
    public PositionMasterData findByIdPosition(Integer idPosition) {
        return repository.findByIdPosition(idPosition);
    }

    @Transactional(readOnly = true)
    public PositionMasterData findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<PositionMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public PositionMasterData saveOrUpdate(PositionMasterData positionMasterData) {
        return repository.save(positionMasterData);
    }

    @Transactional
    public void delete(PositionMasterData positionMasterData) {
        repository.delete(positionMasterData);
    }

}
