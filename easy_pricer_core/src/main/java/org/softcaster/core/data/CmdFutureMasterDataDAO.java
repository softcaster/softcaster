package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CmdFutureMasterDataDAO extends AbstractMasterDataDAO<CmdFutureMasterData, CmdFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public CmdFutureMasterDataDAO(CmdFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CmdFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
}
