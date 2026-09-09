package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MmFutureMasterDataDAO2  extends AbstractMasterDataDAO<MmFutureMasterData2, MmFutureMasterDataRepository2> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public MmFutureMasterDataDAO2(MmFutureMasterDataRepository2 repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MmFutureMasterData2> findAll() {
        return repository.findAll(sortByCode);
    }
}
