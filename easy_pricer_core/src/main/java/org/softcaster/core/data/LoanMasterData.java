package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "loan_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class LoanMasterData extends MasterData {

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "processing_fees")
    private Double processingFees;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "management_fees")
    private Double managementFees;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "incidental_expenses")
    private Double incidentalExpenses;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "default_interest")
    private Double defaultInterest;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "late_payment_fee")
    private Double latePaymentFee;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "underwriting_fee")
    private Double underwritingFee;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "insurance_premium")
    private Double insurancePremium;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "tax_charges")
    private Double taxCharges;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getProcessingFees() {
        return processingFees;
    }

    public void setProcessingFees(Double processingFees) {
        this.processingFees = processingFees;
    }

    public Double getManagementFees() {
        return managementFees;
    }

    public void setManagementFees(Double managementFees) {
        this.managementFees = managementFees;
    }

    public Double getIncidentalExpenses() {
        return incidentalExpenses;
    }

    public void setIncidentalExpenses(Double incidentalExpenses) {
        this.incidentalExpenses = incidentalExpenses;
    }

    public Double getDefaultInterest() {
        return defaultInterest;
    }

    public void setDefaultInterest(Double defaultInterest) {
        this.defaultInterest = defaultInterest;
    }

    public Double getLatePaymentFee() {
        return latePaymentFee;
    }

    public void setLatePaymentFee(Double latePaymentFee) {
        this.latePaymentFee = latePaymentFee;
    }

    public Double getUnderwritingFee() {
        return underwritingFee;
    }

    public void setUnderwritingFee(Double underwritingFee) {
        this.underwritingFee = underwritingFee;
    }

    public Double getInsurancePremium() {
        return insurancePremium;
    }

    public void setInsurancePremium(Double insurancePremium) {
        this.insurancePremium = insurancePremium;
    }

    public Double getTaxCharges() {
        return taxCharges;
    }

    public void setTaxCharges(Double taxCharges) {
        this.taxCharges = taxCharges;
    }

}
