package org.softcaster.easy_pricer_core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "future_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class FutureMasterData extends MasterData {

    @Column(name = "isin")
    private String isin;

    @Column(name = "exchange_contract_code")
    private String exchangeContractCode;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "settlement_type", nullable = true)
    private SettlementType settlementType;

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    /**
     * @return the settlementType
     */
    public SettlementType getSettlementType() {
        return settlementType;
    }

    /**
     * @param settlementType the settlementType to set
     */
    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    /**
     * @return the exchangeContractCode
     */
    public String getExchangeContractCode() {
        return exchangeContractCode;
    }

    /**
     * @param exchangeContractCode the exchangeContractCode to set
     */
    public void setExchangeContractCode(String exchangeContractCode) {
        this.exchangeContractCode = exchangeContractCode;
    }
    
    
}
