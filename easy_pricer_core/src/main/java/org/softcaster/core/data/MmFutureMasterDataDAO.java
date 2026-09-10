package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MmFutureMasterDataDAO  extends AbstractMasterDataDAO<MmFutureMasterData, MmFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public MmFutureMasterDataDAO(MmFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MmFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
