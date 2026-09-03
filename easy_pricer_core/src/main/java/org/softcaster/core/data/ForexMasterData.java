package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forex_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class ForexMasterData extends MasterData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bcy", nullable = true)
    private Currency bcy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccy", nullable = true)
    private Currency ccy;

    @Column(name = "bcy_irc")
    private String bcyIrc;

    @Column(name = "ccy_irc")
    private String ccyIrc;

    /**
     * @return the bcy
     */
    public Currency getBcy() {
        return bcy;
    }

    /**
     * @param bcy the bcy to set
     */
    public void setBcy(Currency bcy) {
        this.bcy = bcy;
    }

    /**
     * @return the ccy
     */
    public Currency getCcy() {
        return ccy;
    }

    /**
     * @param ccy the ccy to set
     */
    public void setCcy(Currency ccy) {
        this.ccy = ccy;
    }

    /**
     * @return the bcyIrc
     */
    public String getBcyIrc() {
        return bcyIrc;
    }

    /**
     * @param bcyIrc the bcyIrc to set
     */
    public void setBcyIrc(String bcyIrc) {
        this.bcyIrc = bcyIrc;
    }

    /**
     * @return the ccyIrc
     */
    public String getCcyIrc() {
        return ccyIrc;
    }

    /**
     * @param ccyIrc the ccyIrc to set
     */
    public void setCcyIrc(String ccyIrc) {
        this.ccyIrc = ccyIrc;
    }

    @Override
    public String toString() {
        return getCode();
    }

    @Override
    public List<Currency> getCurrencyList() {
        List<Currency> currencies = null;
        if (bcy != null && ccy != null) {
            currencies = new ArrayList<>();
            currencies.add(bcy);
            currencies.add(ccy);
        }

        return currencies;
    }
    
        @Override
        public Currency getSettlementCcy() {
            return ccy;
        }

        @Override
        public Currency getMasterDataCcy() {
            return bcy;
        }
}
