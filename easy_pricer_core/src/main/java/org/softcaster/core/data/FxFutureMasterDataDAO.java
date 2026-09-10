package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FxFutureMasterDataDAO extends AbstractMasterDataDAO<FxFutureMasterData, FxFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public FxFutureMasterDataDAO(FxFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FxFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
