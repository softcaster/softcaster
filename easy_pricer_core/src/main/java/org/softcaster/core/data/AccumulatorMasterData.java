package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.AccumulatorTypeConverter;
import org.softcaster.core.data.converters.BarrierObservationConverter;
import org.softcaster.core.data.converters.FrequencyConverter;
import org.softcaster.core.data.converters.KnockInEffectConverter;
import org.softcaster.core.data.converters.SettlementTypeConverter;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.KnockInEffect;
import org.softcaster.engine.enums.SettlementType;

/**
 * Accumulator / decumulator OTC: ogni contratto e' un'anagrafica, creata dal
 * deal capture insieme al calendario dei fixing e alla transazione.
 *
 * A ogni fixing chi detiene il contratto compra (accumulator) o vende
 * (decumulator) dailyQuantity allo strike, leverage volte nei fixing
 * sfavorevoli. Knock-out obbligatorio dalla parte favorevole; knock-in
 * facoltativo dalla parte opposta, che attiva la leva o il contratto.
 *
 * Asset class FXA (sottostante una coppia di cambi) o CMA (uno strumento
 * commodity). In master_data: currency = valuta di regolamento, issue_date =
 * conclusione, maturity_date = ultimo regolamento, business_days = spot lag.
 */
@Entity
@Table(name = "accumulator_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "AccumulatorMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode("currency"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode("underlying"),
            @NamedAttributeNode("fixings")
        }
)
public class AccumulatorMasterData extends MasterData {

    public static final String FX_ASSET_CLASS = "FXA";
    public static final String COMMODITY_ASSET_CLASS = "CMA";

    private static final int CODE_LENGTH = 25;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "underlying", nullable = false)
    private MasterData underlying;

    @Convert(converter = AccumulatorTypeConverter.class)
    @Column(name = "accumulator_type", nullable = false)
    private AccumulatorType accumulatorType;

    @Convert(converter = SettlementTypeConverter.class)
    @Column(name = "settlement_type", nullable = false)
    private SettlementType settlementType;

    @Convert(converter = FrequencyConverter.class)
    @Column(name = "settlement_frequency", nullable = false)
    private Frequency settlementFrequency;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "strike", nullable = false)
    private Double strike;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ko_barrier", nullable = false)
    private Double koBarrier;

    @Convert(converter = BarrierObservationConverter.class)
    @Column(name = "ko_observation", nullable = false)
    private BarrierObservation koObservation = BarrierObservation.FIXING;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ki_barrier")
    private Double kiBarrier;

    @Convert(converter = KnockInEffectConverter.class)
    @Column(name = "ki_effect")
    private KnockInEffect kiEffect;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "leverage", nullable = false)
    private Double leverage = 1.;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "daily_quantity", nullable = false)
    private Double dailyQuantity;

    @Column(name = "first_fixing_date", nullable = false)
    private java.sql.Date firstFixingDate;

    @Column(name = "last_fixing_date", nullable = false)
    private java.sql.Date lastFixingDate;

    @Column(name = "guaranteed_fixings", nullable = false)
    private Integer guaranteedFixings = 0;

    @Column(name = "ko_accrual", nullable = false)
    private Boolean koAccrual = false;

    @Column(name = "fixing_time")
    private LocalTime fixingTime;

    @Column(name = "fixing_time_zone")
    private String fixingTimeZone;

    @Column(name = "ko_date")
    private java.sql.Date koDate;

    @Column(name = "ki_date")
    private java.sql.Date kiDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table accumulator_fixing
    @OrderBy("fixingDate")
    private List<AccumulatorFixing> fixings = new ArrayList<>();

    /**
     * Codice del contratto: asset class, sottostante e id (FXA-EURUSD-123); il
     * codice del sottostante si accorcia per stare nei 25 caratteri.
     */
    public String contractCode() {
        Objects.requireNonNull(getIdMasterData(), "Contratto non ancora salvato");
        String prefix = getAssetClass().getCode() + "-";
        String suffix = "-" + getIdMasterData();
        String underlyingCode = underlying.getCode();
        int room = CODE_LENGTH - prefix.length() - suffix.length();
        if (underlyingCode.length() > room) {
            underlyingCode = underlyingCode.substring(0, Math.max(room, 0));
        }
        return prefix + underlyingCode + suffix;
    }

    @PrePersist
    @PreUpdate
    private void checkContract() {
        Objects.requireNonNull(underlying, "Accumulator " + label() + ": sottostante mancante");
        Objects.requireNonNull(accumulatorType, "Accumulator " + label() + ": tipo mancante");
        Objects.requireNonNull(strike, "Accumulator " + label() + ": strike mancante");
        Objects.requireNonNull(koBarrier, "Accumulator " + label() + ": barriera di knock-out mancante");
        String assetClass = getAssetClass() == null ? null : getAssetClass().getCode();
        if (FX_ASSET_CLASS.equals(assetClass) && !(Hibernate.unproxy(underlying) instanceof ForexMasterData)) {
            throw new IllegalStateException("Accumulator " + label() + ": il sottostante " + underlying.getCode()
                    + " non e' una coppia di cambi");
        }
        if (!accumulatorType.isFavourable(koBarrier, strike)) {
            throw new IllegalStateException("Accumulator " + label() + ": la barriera di knock-out deve stare "
                    + (accumulatorType == AccumulatorType.ACCUMULATOR ? "sopra" : "sotto") + " lo strike");
        }
        if ((kiBarrier == null) != (kiEffect == null)) {
            throw new IllegalStateException("Accumulator " + label() + ": livello ed effetto del knock-in vanno indicati insieme");
        }
        if (kiBarrier != null && !accumulatorType.isFavourable(strike, kiBarrier)) {
            throw new IllegalStateException("Accumulator " + label() + ": la barriera di knock-in deve stare "
                    + (accumulatorType == AccumulatorType.ACCUMULATOR ? "sotto" : "sopra") + " lo strike");
        }
    }

    // nei messaggi: il codice provvisorio di un contratto nuovo non dice nulla
    private String label() {
        return getIdMasterData() == null ? "nuovo" : getCode();
    }

    /**
     * Calendario: le due valute della coppia per FXA, la valuta del contratto
     * per CMA.
     */
    @Override
    public List<Currency> getCurrencyList() {
        if (Hibernate.unproxy(underlying) instanceof ForexMasterData fmd) {
            return fmd.getCurrencyList();
        }
        return super.getCurrencyList();
    }

    public boolean isKnockedOut() {
        return koDate != null;
    }

    public boolean isKnockedIn() {
        return kiDate != null;
    }

    /**
     * Quantita' massima senza leva: dailyQuantity per il numero di fixing.
     */
    public double getMaxQuantity() {
        return dailyQuantity * fixings.size();
    }

    public MasterData getUnderlying() {
        return underlying;
    }

    public void setUnderlying(MasterData underlying) {
        this.underlying = underlying;
    }

    public AccumulatorType getAccumulatorType() {
        return accumulatorType;
    }

    public void setAccumulatorType(AccumulatorType accumulatorType) {
        this.accumulatorType = accumulatorType;
    }

    public SettlementType getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }

    public Frequency getSettlementFrequency() {
        return settlementFrequency;
    }

    public void setSettlementFrequency(Frequency settlementFrequency) {
        this.settlementFrequency = settlementFrequency;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    public Double getKoBarrier() {
        return koBarrier;
    }

    public void setKoBarrier(Double koBarrier) {
        this.koBarrier = koBarrier;
    }

    public BarrierObservation getKoObservation() {
        return koObservation;
    }

    public void setKoObservation(BarrierObservation koObservation) {
        this.koObservation = koObservation;
    }

    public Double getKiBarrier() {
        return kiBarrier;
    }

    public void setKiBarrier(Double kiBarrier) {
        this.kiBarrier = kiBarrier;
    }

    public KnockInEffect getKiEffect() {
        return kiEffect;
    }

    public void setKiEffect(KnockInEffect kiEffect) {
        this.kiEffect = kiEffect;
    }

    public Double getLeverage() {
        return leverage;
    }

    public void setLeverage(Double leverage) {
        this.leverage = leverage;
    }

    public Double getDailyQuantity() {
        return dailyQuantity;
    }

    public void setDailyQuantity(Double dailyQuantity) {
        this.dailyQuantity = dailyQuantity;
    }

    public java.sql.Date getFirstFixingDate() {
        return firstFixingDate;
    }

    public void setFirstFixingDate(java.sql.Date firstFixingDate) {
        this.firstFixingDate = firstFixingDate;
    }

    public LocalDate getFirstFixingLocalDate() {
        return firstFixingDate == null ? null : firstFixingDate.toLocalDate();
    }

    public java.sql.Date getLastFixingDate() {
        return lastFixingDate;
    }

    public void setLastFixingDate(java.sql.Date lastFixingDate) {
        this.lastFixingDate = lastFixingDate;
    }

    public LocalDate getLastFixingLocalDate() {
        return lastFixingDate == null ? null : lastFixingDate.toLocalDate();
    }

    public Integer getGuaranteedFixings() {
        return guaranteedFixings;
    }

    public void setGuaranteedFixings(Integer guaranteedFixings) {
        this.guaranteedFixings = guaranteedFixings;
    }

    public Boolean getKoAccrual() {
        return koAccrual;
    }

    public void setKoAccrual(Boolean koAccrual) {
        this.koAccrual = koAccrual;
    }

    public LocalTime getFixingTime() {
        return fixingTime;
    }

    public void setFixingTime(LocalTime fixingTime) {
        this.fixingTime = fixingTime;
    }

    public String getFixingTimeZone() {
        return fixingTimeZone;
    }

    public void setFixingTimeZone(String fixingTimeZone) {
        this.fixingTimeZone = fixingTimeZone;
    }

    public java.sql.Date getKoDate() {
        return koDate;
    }

    public void setKoDate(java.sql.Date koDate) {
        this.koDate = koDate;
    }

    public java.sql.Date getKiDate() {
        return kiDate;
    }

    public void setKiDate(java.sql.Date kiDate) {
        this.kiDate = kiDate;
    }

    public List<AccumulatorFixing> getFixings() {
        return fixings;
    }

    public void setFixings(List<AccumulatorFixing> fixings) {
        this.fixings = fixings;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
