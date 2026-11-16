package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.ExerciseStyleConverter;
import org.softcaster.core.data.converters.OptionTypeConverter;
import org.softcaster.core.data.converters.SettlementTypeConverter;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.enums.SettlementType;

/**
 * Opzione OTC: ogni contratto e' un'anagrafica a se', creata dal deal
 * capture (nessuna serie ne' testata, a differenza delle opzioni quotate).
 * Qui i termini comuni; le foglie cambiano solo il sottostante:
 *  - OtcFxOptionMasterData  (OFX): coppia di cambi;
 *  - OtcCmdOptionMasterData (OCM): scadenza di un future su commodity.
 *
 * Campi di master_data: maturity_date = scadenza dell'opzione, issue_date =
 * data dell'operazione, currency = valuta di regolamento del premio e del
 * payoff (per le FX la valuta quotata della coppia), business_days = spot
 * lag del premio e della consegna.
 *
 * notional: quantita' del sottostante (valuta base per le FX, unita' della
 * commodity), per contratto. La transazione e' sempre di 1 contratto al
 * prezzo = premio totale nella valuta di regolamento.
 *
 * deliveryDate: consegna del sottostante all'esercizio (FX: scadenza + spot
 * lag se non indicata).
 */
@Entity
@Table(name = "otc_option_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class OtcOptionMasterData extends MasterData {

    public static final String FX_ASSET_CLASS = "OFX";
    public static final String COMMODITY_ASSET_CLASS = "OCM";

    private static final int CODE_LENGTH = 25;

    @Convert(converter = OptionTypeConverter.class)
    @Column(name = "option_type", nullable = false)
    private OptionType optionType;

    @Convert(converter = ExerciseStyleConverter.class)
    @Column(name = "exercise_style", nullable = false)
    private ExerciseStyle exerciseStyle = ExerciseStyle.EUROPEAN;

    @Convert(converter = SettlementTypeConverter.class)
    @Column(name = "settlement_type", nullable = false)
    private SettlementType settlementType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "strike", nullable = false)
    private Double strike;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "notional", nullable = false)
    private Double notional;

    @Column(name = "expiry_time")
    private LocalTime expiryTime;

    @Column(name = "expiry_time_zone")
    private String expiryTimeZone;

    @Column(name = "delivery_date")
    private java.sql.Date deliveryDate;

    public abstract MasterData getUnderlying();

    protected abstract void checkUnderlying();

    /**
     * Codice del contratto, dopo il salvataggio: asset class, sottostante,
     * tipo e id (OFX-EURUSD-C-1234).
     */
    public String contractCode() {
        Objects.requireNonNull(getIdMasterData(), "Contract not saved yet");
        String prefix = getAssetClass().getCode() + "-";
        String suffix = "-" + (optionType == OptionType.CALL ? "C" : "P") + "-" + getIdMasterData();
        String underlyingCode = getUnderlying().getCode();
        int room = CODE_LENGTH - prefix.length() - suffix.length();
        if (underlyingCode.length() > room) {
            underlyingCode = underlyingCode.substring(0, Math.max(room, 0));
        }
        return prefix + underlyingCode + suffix;
    }

    @PrePersist
    @PreUpdate
    private void checkContract() {
        Objects.requireNonNull(getUnderlying(), "Option " + label() + ": missing underlying");
        Objects.requireNonNull(optionType, "Option " + label() + ": missing option type");
        Objects.requireNonNull(settlementType, "Option " + label() + ": missing settlement type");
        if (exerciseStyle == ExerciseStyle.BERMUDAN) {
            throw new IllegalStateException("Option " + label() + ": Bermudan exercise not supported");
        }
        if (strike == null || strike <= 0) {
            throw new IllegalStateException("Option " + label() + ": strike must be positive");
        }
        if (notional == null || notional <= 0) {
            throw new IllegalStateException("Option " + label() + ": notional must be positive");
        }
        if (getMaturityDate() == null) {
            throw new IllegalStateException("Option " + label() + ": missing expiry date");
        }
        if (getIssueDate() != null && getMaturityDate().before(getIssueDate())) {
            throw new IllegalStateException("Option " + label() + ": expiry before the trade date");
        }
        if (deliveryDate != null && deliveryDate.before(getMaturityDate())) {
            throw new IllegalStateException("Option " + label() + ": delivery before the expiry date");
        }
        checkUnderlying();
    }

    // nei messaggi: il codice provvisorio di un contratto nuovo non dice nulla
    protected String label() {
        return getIdMasterData() == null ? "new" : getCode();
    }

    public LocalDate getExpiryLocalDate() {
        return getMaturityDate() == null ? null : getMaturityDate().toLocalDate();
    }

    /**
     * Istante di scadenza (cut): ora e fuso del contratto, fine giornata UTC
     * se non indicati.
     */
    public ZonedDateTime getExpiryInstant() {
        ZoneId zone = expiryTimeZone == null || expiryTimeZone.isBlank() ? ZoneId.of("UTC") : ZoneId.of(expiryTimeZone);
        LocalTime time = expiryTime == null ? LocalTime.MAX : expiryTime;
        return ZonedDateTime.of(getExpiryLocalDate(), time, zone);
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public ExerciseStyle getExerciseStyle() {
        return exerciseStyle;
    }

    public void setExerciseStyle(ExerciseStyle exerciseStyle) {
        this.exerciseStyle = exerciseStyle;
    }

    public SettlementType getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    public Double getNotional() {
        return notional;
    }

    public void setNotional(Double notional) {
        this.notional = notional;
    }

    public LocalTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getExpiryTimeZone() {
        return expiryTimeZone;
    }

    public void setExpiryTimeZone(String expiryTimeZone) {
        this.expiryTimeZone = expiryTimeZone;
    }

    public java.sql.Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(java.sql.Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDate getDeliveryLocalDate() {
        return deliveryDate == null ? null : deliveryDate.toLocalDate();
    }

    @Override
    public String toString() {
        return getCode();
    }
}
