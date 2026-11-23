package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Indice azionario (SX5E): sottostante di future (IXF) e opzioni (IXO).
 *
 * Come le coppie di cambi non scade: maturity_date e' una data lontana. Il
 * forward all'istante T e' spot x exp((r - q) T), con r dalla curva irc e q
 * il dividend yield continuo; l'import della superficie Eurex riporta nel log
 * quello implicito nei prezzi per allinearlo.
 */
@Entity
@Table(name = "index_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public class IndexMasterData extends MasterData {

    // curva di sconto della valuta dell'indice (ESTR_OIS per SX5E)
    @Column(name = "irc")
    private String irc;

    // dividend yield continuo annuo, in decimale
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "dividend_yield")
    private Double dividendYield = 0.;

    public String getIrc() {
        return irc;
    }

    public void setIrc(String irc) {
        this.irc = irc;
    }

    public Double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
