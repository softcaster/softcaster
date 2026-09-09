package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BondFutureMasterDataDAO2 extends AbstractMasterDataDAO<BondFutureMasterData2, BondFutureMasterDataRepository2> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public BondFutureMasterDataDAO2(BondFutureMasterDataRepository2 repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BondFutureMasterData2> findAll() {
        return repository.findAll(sortByCode);
    }
}
