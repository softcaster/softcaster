package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.AccrualScheduleTypeConverter;
import org.softcaster.core.data.converters.AmortizationScheduleConverter;
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.core.data.converters.FormConverter;
import org.softcaster.core.data.converters.FrequencyConverter;
import org.softcaster.core.data.converters.RollConventionConverter;
import org.softcaster.core.data.converters.TypeOfInterestConverter;
import org.softcaster.engine.enums.AccrualScheduleType;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Form;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.RollConvention;
import org.softcaster.engine.enums.TypeOfInterest;

@Entity
@Table(name = "master_data")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(
        strategy = InheritanceType.JOINED
)

public class MasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "master_data_seq", sequenceName = "master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "master_data_seq")
    @Column(name = "id_master_data")
    private Integer idMasterData;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    // --- RELAZIONE BIDIREZIONALE (LATO INVERSO) ---
    // 1) mappedBy punta al nome del campo Java dentro la classe InstrumentValuation
    @OneToOne(mappedBy = "masterData", fetch = FetchType.LAZY, orphanRemoval = true) 
    private InstrumentValuation instrumentValuation;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_class", nullable = true)
    private AssetClass assetClass;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount")
    private DaycountBasis daycount;

    @Convert(converter = RollConventionConverter.class)
    @Column(name = "roll_convention")
    private RollConvention rollConvention;

    @Convert(converter = FormConverter.class)
    @Column(name = "form")
    private Form form;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "accrual_daycount")
    private DaycountBasis accrualDaycount;

    @Convert(converter = FrequencyConverter.class)
    @Column(name = "frequency")
    private Frequency frequency;

    @Convert(converter = TypeOfInterestConverter.class)
    @Column(name = "type_of_interest")
    private TypeOfInterest typeOfInterest;

    @Convert(converter = AmortizationScheduleConverter.class)
    @Column(name = "amortization_schedule")
    private AmortizationSchedule amortizationSchedule;

    @Convert(converter = AccrualScheduleTypeConverter.class)
    @Column(name = "accrual_schedule_type")
    private AccrualScheduleType accrualScheduleType;

    @Column(name = "issue_date")
    private java.sql.Date issueDate;

    @Column(name = "maturity_date")
    private java.sql.Date maturityDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "interest_rate")
    private Double interestRate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "issue_price")
    private Double issuePrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "redempion_price")
    private Double redempionPrice;

    @Column(name = "business_days")
    private Integer businessDays;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "multiplier")
    private Double multiplier;

    public Integer getIdMasterData() {
        return idMasterData;
    }

    public void setIdMasterData(Integer idMasterData) {
        this.idMasterData = idMasterData;
    }

    public java.sql.Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(java.sql.Date issueDate) {
        this.issueDate = issueDate;
    }

    public java.sql.Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(java.sql.Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(Double issuePrice) {
        this.issuePrice = issuePrice;
    }

    public Double getRedempionPrice() {
        return redempionPrice;
    }

    public void setRedempionPrice(Double redempionPrice) {
        this.redempionPrice = redempionPrice;
    }

    public Integer getBusinessDays() {
        return businessDays;
    }

    public void setBusinessDays(Integer businessDays) {
        this.businessDays = businessDays;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdMasterData() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MasterData that = (MasterData) obj;
        return getIdMasterData().equals(that.getIdMasterData());
    }

    @Override
    public int hashCode() {
        return getIdMasterData() == null ? 0 : idMasterData.hashCode();
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the form
     */
    public Form getForm() {
        return form;
    }

    /**
     * @param form the form to set
     */
    public void setForm(Form form) {
        this.form = form;
    }

    /**
     * @return the frequency
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the rollConvention
     */
    public RollConvention getRollConvention() {
        return rollConvention;
    }

    /**
     * @param rollConvention the rollConvention to set
     */
    public void setRollConvention(RollConvention rollConvention) {
        this.rollConvention = rollConvention;
    }

    /**
     * @return the typeOfInterest
     */
    public TypeOfInterest getTypeOfInterest() {
        return typeOfInterest;
    }

    /**
     * @param typeOfInterest the typeOfInterest to set
     */
    public void setTypeOfInterest(TypeOfInterest typeOfInterest) {
        this.typeOfInterest = typeOfInterest;
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

    /**
     * @return the amortizationSchedule
     */
    public AmortizationSchedule getAmortizationSchedule() {
        return amortizationSchedule;
    }

    /**
     * @param amortizationSchedule the amortizationSchedule to set
     */
    public void setAmortizationSchedule(AmortizationSchedule amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }

    /**
     * @return the assetClass
     */
    public AssetClass getAssetClass() {
        return assetClass;
    }

    /**
     * @param assetClass the assetClass to set
     */
    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the multiplier
     */
    public Double getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier the multiplier to set
     */
    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return the daycount
     */
    public DaycountBasis getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(DaycountBasis daycount) {
        this.daycount = daycount;
    }

    /**
     * @return the accrualDaycount
     */
    public DaycountBasis getAccrualDaycount() {
        return accrualDaycount;
    }

    /**
     * @param accrualDaycount the accrualDaycount to set
     */
    public void setAccrualDaycount(DaycountBasis accrualDaycount) {
        this.accrualDaycount = accrualDaycount;
    }

    /**
     * @return the accrualScheduleType
     */
    public AccrualScheduleType getAccrualScheduleType() {
        return accrualScheduleType;
    }

    /**
     * @param accrualScheduleType the accrualScheduleType to set
     */
    public void setAccrualScheduleType(AccrualScheduleType accrualScheduleType) {
        this.accrualScheduleType = accrualScheduleType;
    }

    /**
     * @return the instrumentValuation
     */
    public InstrumentValuation getInstrumentValuation() {
        return instrumentValuation;
    }

    /**
     * @param instrumentValuation the instrumentValuation to set
     */
    public void setInstrumentValuation(InstrumentValuation instrumentValuation) {
        this.instrumentValuation = instrumentValuation;
    }

    public List<Currency> getCurrencyList() {
        List<Currency> currencies = null;
        if (currency != null) {
            currencies = new ArrayList<>();
            currencies.add(currency);
        }

        return currencies;
    }
}
