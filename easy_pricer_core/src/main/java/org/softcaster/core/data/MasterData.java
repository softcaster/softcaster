package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "master_data")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(
        strategy = InheritanceType.JOINED
)

public class MasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "master_data_seq", sequenceName = "master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "master_data_seq")
    @Column(name = "id_master_data", columnDefinition = "INTEGER")
    private Integer idMasterData;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @Column(name = "issue_date")
    private java.sql.Date issueDate;

    @Column(name = "maturity_date")
    private java.sql.Date maturityDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_of_interest", nullable = true)
    private TypeOfInterest typeOfInterest;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form", nullable = true)
    private Form form;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "daycount", nullable = true)
    private Daycount daycount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accrual_daycount", nullable = true)
    private Daycount accrualDaycount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "frequency", nullable = true)
    private Frequency frequency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roll_convention", nullable = true)
    private RollConvention rollConvention;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_class", nullable = true)
    private AssetClass assetClass;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "amortization_schedule", nullable = true)
    private AmortizationSchedule amortizationSchedule;

    @Column(name = "accrual_schedule_type")
    private Integer accrualScheduleType;

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

    public Integer getAccrualScheduleType() {
        return accrualScheduleType;
    }

    public void setAccrualScheduleType(Integer accrualScheduleType) {
        this.accrualScheduleType = accrualScheduleType;
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
     * @return the daycount
     */
    public Daycount getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(Daycount daycount) {
        this.daycount = daycount;
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
     * @return the accrualDaycount
     */
    public Daycount getAccrualDaycount() {
        return accrualDaycount;
    }

    /**
     * @param accrualDaycount the accrualDaycount to set
     */
    public void setAccrualDaycount(Daycount accrualDaycount) {
        this.accrualDaycount = accrualDaycount;
    }
}
