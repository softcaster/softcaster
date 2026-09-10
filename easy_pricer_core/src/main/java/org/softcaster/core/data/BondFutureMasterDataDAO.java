package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BondFutureMasterDataDAO extends AbstractMasterDataDAO<BondFutureMasterData, BondFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public BondFutureMasterDataDAO(BondFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BondFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    public Optional<BondFutureMasterData> findByIsin(String isin) {
        return repository.findByIsin(isin);
    }    

}
