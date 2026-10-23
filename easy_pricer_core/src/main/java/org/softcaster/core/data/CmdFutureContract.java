package org.softcaster.core.data;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.softcaster.engine.enums.ContractType;

/**
 * Scadenza di un future su commodity. Testata: CmdFutureMasterData (comprese
 * le sottoclassi, es. PowerFutureMasterData).
 */
@Entity
@DiscriminatorValue(ContractType.CMD_FUTURE_ID)
@SuppressWarnings("PersistenceUnitPresent")
public class CmdFutureContract extends FutureContract {

    private static final long serialVersionUID = 1L;

    @Override
    public ContractType getContractType() {
        return ContractType.CMD_FUTURE;
    }

    @Override
    protected Class<? extends FutureMasterData> productType() {
        return CmdFutureMasterData.class;
    }

    /**
     * Testata del future su commodity.
     *
     * @return
     */
    public CmdFutureMasterData getCmdFutureMasterData() {
        return getMasterData(CmdFutureMasterData.class);
    }
}
