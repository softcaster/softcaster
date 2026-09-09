package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// @Repository abilita la traduzione automatica delle eccezioni di persistenza, a differenza di @Service
// Quindi e`preferibile per oggetti DAO
@Repository
public class ForexMasterDataDAO2
        extends AbstractMasterDataDAO<
                ForexMasterData2, ForexMasterDataRepository2> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public ForexMasterDataDAO2(
            ForexMasterDataRepository2 repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ForexMasterData2> findAll() {
        return repository.findAll(sortByCode);
    }
    
}
