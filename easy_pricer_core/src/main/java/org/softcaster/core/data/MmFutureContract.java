package org.softcaster.core.data;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.softcaster.engine.enums.ContractType;

/**
 * Scadenza di un future money market. Testata: MmFutureMasterData.
 */
@Entity
@DiscriminatorValue(ContractType.MM_FUTURE_ID)
@SuppressWarnings("PersistenceUnitPresent")
public class MmFutureContract extends FutureContract {

    private static final long serialVersionUID = 1L;

    @Override
    public ContractType getContractType() {
        return ContractType.MM_FUTURE;
    }

    @Override
    protected Class<? extends FutureMasterData> productType() {
        return MmFutureMasterData.class;
    }

    /**
     * Testata del future money market.
     *
     * @return
     */
    public MmFutureMasterData getMmFutureMasterData() {
        return getMasterData(MmFutureMasterData.class);
    }
}
