package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.AccrualScheduleTypeConverter;
import org.softcaster.core.data.converters.AmortizationScheduleConverter;
import org.softcaster.core.data.converters.CompoundingConverter;
import org.softcaster.core.data.converters.DaycountConverter;
import org.softcaster.core.data.converters.FormConverter;
import org.softcaster.core.data.converters.FrequencyConverter;
import org.softcaster.core.data.converters.RollConventionConverter;
import org.softcaster.core.data.converters.TypeOfInterestConverter;
import org.softcaster.engine.enums.AccrualScheduleType;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Form;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.RollConvention;
import org.softcaster.engine.enums.TypeOfInterest;

/**
 * Anagrafica di uno strumento che genera flussi (titoli, mutui): convenzioni
 * di calcolo di cedole e ratei, prezzi di emissione e rimborso, piano dei
 * flussi (cash_flow_item).
 *
 * Livello intermedio della gerarchia JOINED di master_data:
 * master_data -> cash_flow_master_data -> security_master_data / loan_master_data.
 */
@Entity
@Table(name = "cash_flow_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class CashFlowMasterData extends MasterData {

    @Convert(converter = TypeOfInterestConverter.class)
    @Column(name = "type_of_interest")
    private TypeOfInterest typeOfInterest;

    @Convert(converter = FormConverter.class)
    @Column(name = "form")
    private Form form;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "daycount")
    private DaycountBasis daycount;

    @Convert(converter = DaycountConverter.class)
    @Column(name = "accrual_daycount")
    private DaycountBasis accrualDaycount;

    @Convert(converter = FrequencyConverter.class)
    @Column(name = "frequency")
    private Frequency frequency;

    @Convert(converter = RollConventionConverter.class)
    @Column(name = "roll_convention")
    private RollConvention rollConvention;

    @Convert(converter = AccrualScheduleTypeConverter.class)
    @Column(name = "accrual_schedule_type")
    private AccrualScheduleType accrualScheduleType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "interest_rate")
    private Double interestRate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "issue_price")
    private Double issuePrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "redempion_price")
    private Double redempionPrice;

    @Convert(converter = AmortizationScheduleConverter.class)
    @Column(name = "amortization_schedule")
    private AmortizationSchedule amortizationSchedule;

    @Convert(converter = CompoundingConverter.class)
    @Column(name = "compounding")
    private Compounding compounding;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table cash_flow_item
    private List<CashFlowItem> cashFlows = new ArrayList<>();

    public TypeOfInterest getTypeOfInterest() {
        return typeOfInterest;
    }

    public void setTypeOfInterest(TypeOfInterest typeOfInterest) {
        this.typeOfInterest = typeOfInterest;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public DaycountBasis getDaycount() {
        return daycount;
    }

    public void setDaycount(DaycountBasis daycount) {
        this.daycount = daycount;
    }

    public DaycountBasis getAccrualDaycount() {
        return accrualDaycount;
    }

    public void setAccrualDaycount(DaycountBasis accrualDaycount) {
        this.accrualDaycount = accrualDaycount;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public RollConvention getRollConvention() {
        return rollConvention;
    }

    public void setRollConvention(RollConvention rollConvention) {
        this.rollConvention = rollConvention;
    }

    public AccrualScheduleType getAccrualScheduleType() {
        return accrualScheduleType;
    }

    public void setAccrualScheduleType(AccrualScheduleType accrualScheduleType) {
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

    public AmortizationSchedule getAmortizationSchedule() {
        return amortizationSchedule;
    }

    public void setAmortizationSchedule(AmortizationSchedule amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }

    public Compounding getCompounding() {
        return compounding;
    }

    public void setCompounding(Compounding compounding) {
        this.compounding = compounding;
    }

    public List<CashFlowItem> getCashFlows() {
        return cashFlows;
    }

    public void setCashFlows(List<CashFlowItem> cashFlows) {
        this.cashFlows = cashFlows;
    }
}
