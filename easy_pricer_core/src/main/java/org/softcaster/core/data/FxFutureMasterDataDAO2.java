package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FxFutureMasterDataDAO2 extends AbstractMasterDataDAO<
                FxFutureMasterData2, FxFutureMasterDataRepository2> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public FxFutureMasterDataDAO2(
            FxFutureMasterDataRepository2 repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FxFutureMasterData2> findAll() {
        return repository.findAll(sortByCode);
    }
}
