package org.softcaster.easy_pricer_core.data;

import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "bond_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class BondFutureMasterData extends FutureMasterData {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "contract_value")
    private Double contractValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "tick_size")
    private Double tickSize;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "initial_margin")
    private Double initialMargin;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table deliverable_bonds
    private List<DeliverableBonds> deliverables = new ArrayList<>();

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

    /**
     * @return the deliverables
     */
    public List<DeliverableBonds> getDeliverables() {
        return deliverables;
    }

    /**
     * @param deliverables the deliverables to set
     */
    public void setDeliverables(List<DeliverableBonds> deliverables) {
        this.deliverables = deliverables;
    }

    @Override
    public String toString() {
        return getCode();
    }    
}
