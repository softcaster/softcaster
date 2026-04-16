package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "instrument_quote")
@SuppressWarnings("PersistenceUnitPresent")

public class InstrumentQuote implements Serializable {

    @Id
    @SequenceGenerator(name = "instrument_quote_seq", sequenceName = "instrument_quote_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "instrument_quote_seq")
    @Column(name = "id_instrument_quote", columnDefinition = "INTEGER")
    private Integer idInstrumentQuote;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "master_data", nullable = true)
    private MasterData masterData;

    @Column(name = "code")
    private String code;

    @Column(name = "provider")
    private String provider;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "bid")
    private Double bid;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ask")
    private Double ask;

    public Integer getIdInstrumentQuote() {
        return idInstrumentQuote;
    }

    public void setIdInstrumentQuote(Integer idInstrumentQuote) {
        this.idInstrumentQuote = idInstrumentQuote;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdInstrumentQuote() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InstrumentQuote that = (InstrumentQuote) obj;
        return getIdInstrumentQuote().equals(that.getIdInstrumentQuote());
    }

    @Override
    public int hashCode() {
        return getIdInstrumentQuote() == null ? 0 : idInstrumentQuote.hashCode();
    }

    /**
     * @return the masterData
     */
    public MasterData getMasterData() {
        return masterData;
    }

    /**
     * @param masterData the masterData to set
     */
    public void setMasterData(MasterData masterData) {
        this.masterData = masterData;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }
}
