package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.FeeBasisConverter;
import org.softcaster.core.data.converters.FeeTimingConverter;
import org.softcaster.core.data.converters.LoanFeeTypeConverter;
import org.softcaster.engine.enums.FeeBasis;
import org.softcaster.engine.enums.FeeTiming;
import org.softcaster.engine.enums.LoanFeeType;

/**
 * Spesa di un mutuo: importo (AMOUNT, valuta del mutuo) o percentuale in
 * punti (PERCENT, 0.25 = 0,25%) del capitale (UPFRONT) o del debito residuo a
 * inizio periodo (PER_INSTALLMENT, per rata). Una per tipo e mutuo.
 */
@Entity
@Table(name = "loan_fee")
@SuppressWarnings("PersistenceUnitPresent")
public class LoanFee implements Serializable {

    @Id
    @SequenceGenerator(name = "loan_fee_seq", sequenceName = "loan_fee_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_fee_seq")
    @Column(name = "id_loan_fee")
    private Integer idLoanFee;

    /**
     * Mutuo: valorizzato da Hibernate tramite il @JoinColumn di
     * LoanMasterData.fees.
     */
    @Column(name = "master_data", insertable = false, updatable = false)
    private Integer masterData;

    @Convert(converter = LoanFeeTypeConverter.class)
    @Column(name = "fee_type", nullable = false)
    private LoanFeeType feeType;

    @Convert(converter = FeeBasisConverter.class)
    @Column(name = "fee_basis", nullable = false)
    private FeeBasis feeBasis;

    @Convert(converter = FeeTimingConverter.class)
    @Column(name = "fee_timing", nullable = false)
    private FeeTiming feeTiming;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "value", nullable = false)
    private Double value;

    public LoanFee() {
    }

    public LoanFee(LoanFeeType feeType, FeeBasis feeBasis, FeeTiming feeTiming, double value) {
        this.feeType = feeType;
        this.feeBasis = feeBasis;
        this.feeTiming = feeTiming;
        this.value = value;
    }

    public Integer getIdLoanFee() {
        return idLoanFee;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public LoanFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(LoanFeeType feeType) {
        this.feeType = feeType;
    }

    public FeeBasis getFeeBasis() {
        return feeBasis;
    }

    public void setFeeBasis(FeeBasis feeBasis) {
        this.feeBasis = feeBasis;
    }

    public FeeTiming getFeeTiming() {
        return feeTiming;
    }

    public void setFeeTiming(FeeTiming feeTiming) {
        this.feeTiming = feeTiming;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Uguaglianza per tipo di spesa: una per tipo nel mutuo (uq_loan_fee), e
     * una spesa nuova non ha ancora l'id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (feeType == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return feeType == ((LoanFee) obj).feeType;
    }

    @Override
    public int hashCode() {
        return feeType == null ? 0 : feeType.hashCode();
    }
}
