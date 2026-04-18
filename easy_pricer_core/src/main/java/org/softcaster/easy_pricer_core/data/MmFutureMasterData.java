package org.softcaster.easy_pricer_core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "mm_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class MmFutureMasterData extends FutureMasterData {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "underlying", nullable = true)
    private ForexMasterData underlying;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "contract_value")
    private Double contractValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "tick_size")
    private Double tickSize;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "initial_margin")
    private Double initialMargin;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "maintenance_margin")
    private Double maintenanceMargin;

    public Double getContractValue() {
        return contractValue;
    }

    public void setContractValue(Double contractValue) {
        this.contractValue = contractValue;
    }

    public Double getTickSize() {
        return tickSize;
    }

    public void setTickSize(Double tickSize) {
        this.tickSize = tickSize;
    }

    public Double getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(Double initialMargin) {
        this.initialMargin = initialMargin;
    }

    public Double getMaintenanceMargin() {
        return maintenanceMargin;
    }

    public void setMaintenanceMargin(Double maintenanceMargin) {
        this.maintenanceMargin = maintenanceMargin;
    }

    /**
     * @return the underlying
     */
    public ForexMasterData getUnderlying() {
        return underlying;
    }

    /**
     * @param underlying the underlying to set
     */
    public void setUnderlying(ForexMasterData underlying) {
        this.underlying = underlying;
    }

    @Override
    public String toString() {
        return getCode();
    }    
}
