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
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Data di fixing di un accumulator con la data di regolamento del periodo.
 * Prezzo di fixing e quantita' maturata (leva inclusa) si valorizzano quando
 * il fixing avviene.
 */
@Entity
@Table(name = "accumulator_fixing")
@SuppressWarnings("PersistenceUnitPresent")
public class AccumulatorFixing implements Serializable {

    @Id
    @SequenceGenerator(name = "accumulator_fixing_seq", sequenceName = "accumulator_fixing_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accumulator_fixing_seq")
    @Column(name = "id_accumulator_fixing")
    private Integer idAccumulatorFixing;

    /**
     * Contratto (AccumulatorMasterData): valorizzato da Hibernate tramite il
     * @JoinColumn di AccumulatorMasterData.fixings.
     */
    @Column(name = "master_data", insertable = false, updatable = false)
    private Integer masterData;

    @Column(name = "fixing_date", nullable = false)
    private java.sql.Date fixingDate;

    @Column(name = "settlement_date", nullable = false)
    private java.sql.Date settlementDate;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "fixing_price")
    private Double fixingPrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "accumulated_quantity")
    private Double accumulatedQuantity;

    public AccumulatorFixing() {
    }

    public AccumulatorFixing(LocalDate fixingDate, LocalDate settlementDate) {
        this.fixingDate = java.sql.Date.valueOf(fixingDate);
        this.settlementDate = java.sql.Date.valueOf(settlementDate);
    }

    public Integer getIdAccumulatorFixing() {
        return idAccumulatorFixing;
    }

    public void setIdAccumulatorFixing(Integer idAccumulatorFixing) {
        this.idAccumulatorFixing = idAccumulatorFixing;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public java.sql.Date getFixingDate() {
        return fixingDate;
    }

    public void setFixingDate(java.sql.Date fixingDate) {
        this.fixingDate = fixingDate;
    }

    public LocalDate getFixingLocalDate() {
        return fixingDate == null ? null : fixingDate.toLocalDate();
    }

    public java.sql.Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(java.sql.Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public LocalDate getSettlementLocalDate() {
        return settlementDate == null ? null : settlementDate.toLocalDate();
    }

    public Double getFixingPrice() {
        return fixingPrice;
    }

    public void setFixingPrice(Double fixingPrice) {
        this.fixingPrice = fixingPrice;
    }

    public Double getAccumulatedQuantity() {
        return accumulatedQuantity;
    }

    public void setAccumulatedQuantity(Double accumulatedQuantity) {
        this.accumulatedQuantity = accumulatedQuantity;
    }

    /**
     * Fixing gia' avvenuto.
     */
    public boolean isFixed() {
        return fixingPrice != null;
    }
}
