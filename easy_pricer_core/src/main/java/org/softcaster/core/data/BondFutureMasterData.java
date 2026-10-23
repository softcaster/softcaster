package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Testata di un bond future (es. MBTP). Le singole scadenze, con il paniere
 * consegnabile, sono FutureContract.
 */
@Entity
@Table(name = "bond_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "BondFutureMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode(value = "currency", subgraph = "currencyGraph"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation")
        },
        subgraphs = {
            @NamedSubgraph(name = "currencyGraph", attributeNodes = {
        @NamedAttributeNode(value = "calendar", subgraph = "calendarGraph")
    }),
            @NamedSubgraph(name = "calendarGraph", attributeNodes = {
        @NamedAttributeNode("holidays")
    })
        }
)
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

    @Override
    public String toString() {
        return getCode();
    }
}
