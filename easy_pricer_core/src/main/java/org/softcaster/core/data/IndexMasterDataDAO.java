package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IndexMasterDataDAO extends AbstractMasterDataDAO<IndexMasterData, IndexMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public IndexMasterDataDAO(IndexMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<IndexMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
