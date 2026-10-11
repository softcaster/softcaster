package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "calibration_profile_item")
@SuppressWarnings("PersistenceUnitPresent")

public class CalibrationProfileItem implements Serializable {

    @Id
    @SequenceGenerator(name = "calibration_profile_item_seq", sequenceName = "calibration_profile_item_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calibration_profile_item_seq")
    @Column(name = "calibration_profile_item_id")
    private Integer calibrationProfileItemId;

    @Column(name = "calibration_profile", insertable = false, updatable = false)
    private Integer calibrationProfile;

    @Column(name = "master_data")
    private Integer masterData;

    @Column(name = "instrument_quote")
    private Integer instrumentQuote;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "bid")
    private double bid;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ask")
    private double ask;
    
    @Column(name = "code")
    private String code;

    public Integer getCalibrationProfileItemId() {
        return calibrationProfileItemId;
    }

    public void setCalibrationProfileItemId(Integer calibrationProfileItemId) {
        this.calibrationProfileItemId = calibrationProfileItemId;
    }

    public Integer getCalibrationProfile() {
        return calibrationProfile;
    }

    public void setCalibrationProfile(Integer calibrationProfile) {
        this.calibrationProfile = calibrationProfile;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    /**
     * @return the instrumentQuote
     */
    public Integer getInstrumentQuote() {
        return instrumentQuote;
    }

    /**
     * @param instrumentQuote the instrumentQuote to set
     */
    public void setInstrumentQuote(Integer instrumentQuote) {
        this.instrumentQuote = instrumentQuote;
    }

    /**
     * @return the bid
     */
    public double getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(double bid) {
        this.bid = bid;
    }

    /**
     * @return the ask
     */
    public double getAsk() {
        return ask;
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(double ask) {
        this.ask = ask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalibrationProfileItem that = (CalibrationProfileItem) o;

        // Confronto basato sulla chiave di business (profilo + quota strumento)
        if (calibrationProfile != null ? !calibrationProfile.equals(that.calibrationProfile) : that.calibrationProfile != null) {
            return false;
        }
        return instrumentQuote != null ? instrumentQuote.equals(that.instrumentQuote) : that.instrumentQuote == null;
    }

    @Override
    public int hashCode() {
        int result = calibrationProfile != null ? calibrationProfile.hashCode() : 0;
        result = 31 * result + (instrumentQuote != null ? instrumentQuote.hashCode() : 0);
        return result;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
}
