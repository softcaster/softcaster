package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.engine.enums.FeeTiming;

/**
 * Mutuo OTC: ogni contratto e' un'anagrafica (asset class XRM a tasso fisso,
 * FRM a tasso variabile con FltLoanMasterData), creata dal deal capture.
 *
 * Dal livello cash flow: tasso (interest_rate, punti percentuali), frequenza
 * delle rate, daycount, tipo di piano (AmortizationSchedule: SAS francese,
 * SLP italiano, IOL bullet) e piano delle rate (cashFlows). In master_data:
 * issue_date = erogazione, maturity_date = ultima rata.
 *
 * Qui: capitale finanziato, prima rata, rate di preammortamento (solo
 * interessi), spese (LoanFee: importo o percentuale, una tantum o per rata)
 * e condizioni di servizio fuori dal prezzo (mora, penale per rata).
 */
@Entity
@Table(name = "loan_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "LoanMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode(value = "currency", subgraph = "currencyGraph"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            // un'unica collezione in join (i flussi, una List): le spese con SUBSELECT
            @NamedAttributeNode("cashFlows")
        },
        subgraphs = {
            @NamedSubgraph(name = "currencyGraph", attributeNodes = @NamedAttributeNode("calendar"))
        }
)
public class LoanMasterData extends CashFlowMasterData {

    public static final String FIXED_ASSET_CLASS = "XRM";
    public static final String FLOATING_ASSET_CLASS = "FRM";

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "principal_amount", nullable = false)
    private Double principalAmount;

    // vuota = erogazione + un periodo
    @Column(name = "first_payment_date")
    private java.sql.Date firstPaymentDate;

    // rate iniziali di soli interessi
    @Column(name = "pre_amortization_periods", nullable = false)
    private Integer preAmortizationPeriods = 0;

    // tasso di mora, punti percentuali (fuori dal prezzo)
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "default_interest", nullable = false)
    private Double defaultInterest = 0.;

    // penale per rata pagata in ritardo, importo (fuori dal prezzo)
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "late_payment_fee", nullable = false)
    private Double latePaymentFee = 0.;

    // fuori dagli entity graph (che caricano gia' i flussi): SUBSELECT in transazione
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table loan_fee
    private Set<LoanFee> fees = new LinkedHashSet<>();

    /**
     * Codice del contratto: asset class e id (XRM-123).
     */
    public String contractCode() {
        Objects.requireNonNull(getIdMasterData(), "Mutuo non ancora salvato");
        return getAssetClass().getCode() + "-" + getIdMasterData();
    }

    /**
     * Spese una tantum all'erogazione, in valuta.
     */
    public double upfrontFees() {
        return fees.stream()
                .filter(f -> f.getFeeTiming() == FeeTiming.UPFRONT)
                .mapToDouble(f -> f.getFeeBasis().amount(f.getValue(), principalAmount))
                .sum();
    }

    /**
     * Spese di una rata, dato il debito residuo a inizio periodo.
     */
    public double installmentFees(double outstanding) {
        return fees.stream()
                .filter(f -> f.getFeeTiming() == FeeTiming.PER_INSTALLMENT)
                .mapToDouble(f -> f.getFeeBasis().amount(f.getValue(), outstanding))
                .sum();
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public java.sql.Date getFirstPaymentDate() {
        return firstPaymentDate;
    }

    public void setFirstPaymentDate(java.sql.Date firstPaymentDate) {
        this.firstPaymentDate = firstPaymentDate;
    }

    public LocalDate getFirstPaymentLocalDate() {
        return firstPaymentDate == null ? null : firstPaymentDate.toLocalDate();
    }

    public Integer getPreAmortizationPeriods() {
        return preAmortizationPeriods;
    }

    public void setPreAmortizationPeriods(Integer preAmortizationPeriods) {
        this.preAmortizationPeriods = preAmortizationPeriods;
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

    public Set<LoanFee> getFees() {
        return fees;
    }

    public void setFees(Set<LoanFee> fees) {
        this.fees = fees;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
