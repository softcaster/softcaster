package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class FltSecurityMasterDataDAO2 extends AbstractMasterDataDAO<FltSecurityMasterData2, FltSecurityMasterDataRepository2> {

    public FltSecurityMasterDataDAO2(
            FltSecurityMasterDataRepository2 repository) {
        super(repository);
    }

    public Optional<FltSecurityMasterData2> findByIdWithRefRateIndex(Integer id) {
        return repository.findByIdWithRefRateIndex(id);
    }
}
