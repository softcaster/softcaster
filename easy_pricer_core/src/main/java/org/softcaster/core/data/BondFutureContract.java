package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.engine.enums.ContractType;

/**
 * Scadenza di un bond future (es.MBTP dic-2026): FutureContract con il paniere
 * consegnabile, che dipende dalla singola scadenza.
 *
 * Testata: BondFutureMasterData.
 *
 * Entity graph "BondFutureContract.withProduct": come
 * "FutureContract.withProduct" piu' il paniere.
 */
@Entity
@DiscriminatorValue(ContractType.BOND_FUTURE_ID)
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "BondFutureContract.withProduct",
        attributeNodes = {
            @NamedAttributeNode(value = "masterData", subgraph = "productGraph"),
            @NamedAttributeNode("deliverables")
        },
        subgraphs = {
            @NamedSubgraph(name = "productGraph", attributeNodes = {
        @NamedAttributeNode(value = "currency", subgraph = "currencyGraph"),
        @NamedAttributeNode("assetClass"),
        @NamedAttributeNode("instrumentValuation")
    }),
            @NamedSubgraph(name = "currencyGraph", attributeNodes = {
        @NamedAttributeNode(value = "calendar", subgraph = "calendarGraph")
    }),
            @NamedSubgraph(name = "calendarGraph", attributeNodes = {
        @NamedAttributeNode("holidays")
    })
        }
)
public class BondFutureContract extends FutureContract {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "listed_contract", nullable = false) // FK in child table deliverable_bonds
    private List<DeliverableBonds> deliverables = new ArrayList<>();

    @Override
    public ContractType getContractType() {
        return ContractType.BOND_FUTURE;
    }

    @Override
    protected Class<? extends FutureMasterData> productType() {
        return BondFutureMasterData.class;
    }

    /**
     * Testata del bond future.
     *
     * @return
     */
    public BondFutureMasterData getBondFutureMasterData() {
        return getMasterData(BondFutureMasterData.class);
    }

    public List<DeliverableBonds> getDeliverables() {
        return deliverables;
    }

    public void setDeliverables(List<DeliverableBonds> deliverables) {
        this.deliverables = deliverables;
    }
}
