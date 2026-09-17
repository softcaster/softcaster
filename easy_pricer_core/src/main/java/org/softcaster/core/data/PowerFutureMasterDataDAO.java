package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PowerFutureMasterDataDAO extends AbstractMasterDataDAO<PowerFutureMasterData, PowerFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public PowerFutureMasterDataDAO(PowerFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PowerFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
