package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class FltSecurityMasterDataDAO extends AbstractMasterDataDAO<FltSecurityMasterData, FltSecurityMasterDataRepository> {

    public FltSecurityMasterDataDAO(
            FltSecurityMasterDataRepository repository) {
        super(repository);
    }

    public Optional<FltSecurityMasterData> findByIdWithRefRateIndex(Integer id) {
        return repository.findByIdWithRefRateIndex(id);
    }
}
