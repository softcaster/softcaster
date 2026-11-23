package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IndexFutureMasterDataDAO extends AbstractMasterDataDAO<IndexFutureMasterData, IndexFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public IndexFutureMasterDataDAO(IndexFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<IndexFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
