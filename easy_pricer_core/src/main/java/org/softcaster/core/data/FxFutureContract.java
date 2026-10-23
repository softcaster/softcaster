package org.softcaster.core.data;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.softcaster.engine.enums.ContractType;

/**
 * Scadenza di un future su cambi (M6EZ6). Testata: FxFutureMasterData.
 */
@Entity
@DiscriminatorValue(ContractType.FX_FUTURE_ID)
@SuppressWarnings("PersistenceUnitPresent")
public class FxFutureContract extends FutureContract {

    private static final long serialVersionUID = 1L;

    @Override
    public ContractType getContractType() {
        return ContractType.FX_FUTURE;
    }

    @Override
    protected Class<? extends FutureMasterData> productType() {
        return FxFutureMasterData.class;
    }

    /**
     * Testata del future su cambi.
     *
     * @return
     */
    public FxFutureMasterData getFxFutureMasterData() {
        return getMasterData(FxFutureMasterData.class);
    }
}
