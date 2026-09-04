package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.softcaster.core.data.converters.SettlementTypeConverter;

@Entity
@Table(name = "future_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class FutureMasterData extends MasterData {

    @Column(name = "isin")
    private String isin;

    @Column(name = "exchange_contract_code")
    private String exchangeContractCode;

    @Convert(converter = SettlementTypeConverter.class)
    @Column(name = "settlement_type")
    private org.softcaster.engine.enums.SettlementType settlementType;

    @Column(name = "last_trading_date")
    private java.sql.Date lastTradingDate;
    
    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
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
    
    /**
     * @return the lastTradingDate
     */
    public java.sql.Date getLastTradingDate() {
        return lastTradingDate;
    }

    /**
     * @param lastTradingDate the lastTradingDate to set
     */
    public void setLastTradingDate(java.sql.Date lastTradingDate) {
        this.lastTradingDate = lastTradingDate;
    }

    /**
     * @return the settlementType
     */
    public org.softcaster.engine.enums.SettlementType getSettlementType() {
        return settlementType;
    }

    /**
     * @param settlementType the settlementType to set
     */
    public void setSettlementType(org.softcaster.engine.enums.SettlementType settlementType) {
        this.settlementType = settlementType;
    }
}
