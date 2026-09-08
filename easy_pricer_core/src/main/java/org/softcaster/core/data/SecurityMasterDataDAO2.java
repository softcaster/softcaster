package org.softcaster.core.data;

import org.springframework.stereotype.Repository;

@Repository
public class SecurityMasterDataDAO2
        extends AbstractMasterDataDAO<
                SecurityMasterData2, SecurityMasterDataRepository2> {

    public SecurityMasterDataDAO2(
            SecurityMasterDataRepository2 repository) {
        super(repository);
    }
    
}
