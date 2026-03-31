package org.softcaster.easy_pricer_core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "forex_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class ForexMasterData  extends MasterData {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bcy", nullable = true)
    private Currency bcy;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ccy", nullable = true)
    private Currency ccy;

    @Column(name = "bcy_irc")
    private String bcyIrc;

    @Column(name = "ccy_irc")
    private String ccyIrc;

    public String getBcyIrc() {
        return bcyIrc;
    }

    public void setBcyIrc(String bcyIrc) {
        this.bcyIrc = bcyIrc;
    }

    public String getCcyIrc() {
        return ccyIrc;
    }

    public void setCcyIrc(String ccyIrc) {
        this.ccyIrc = ccyIrc;
    }

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

}
