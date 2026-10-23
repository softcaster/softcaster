package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Scadenza di un bond future (MBTPZ6): testata BondFutureProduct (MBTP) e
 * paniere consegnabile, che dipende dalla singola scadenza.
 *
 * Entity graph "BondFutureMasterData.fullGraph": valuta con calendario e
 * festivita', asset class, valutazione, testata e paniere.
 */
@Entity
@Table(name = "bond_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "BondFutureMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode(value = "currency", subgraph = "currencyGraph"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode("product"),
            @NamedAttributeNode("deliverables")
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // testata sempre disponibile: processori, contabilita', finestre
    @JoinColumn(name = "future_product", nullable = false)
    private BondFutureProduct product;

    // Set e non List: l'entity graph lo carica insieme alle festivita' del calendario,
    // e una List conterrebbe ogni titolo una volta per festivita' (prodotto della join)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table deliverable_bonds
    private Set<DeliverableBonds> deliverables = new LinkedHashSet<>();

    /**
     * Nuova scadenza della testata, con i valori di default della testata.
     *
     * @param product
     * @return
     */
    public static BondFutureMasterData of(BondFutureProduct product) {
        BondFutureMasterData bfmd = new BondFutureMasterData();
        bfmd.setProduct(product);
        bfmd.copyProductDefaults(product);
        return bfmd;
    }

    @Override
    public BondFutureProduct getProduct() {
        return product;
    }

    public void setProduct(BondFutureProduct product) {
        this.product = product;
    }

    @Override
    protected Class<? extends FutureProduct> productType() {
        return BondFutureProduct.class;
    }

    public Set<DeliverableBonds> getDeliverables() {
        return deliverables;
    }

    public void setDeliverables(Set<DeliverableBonds> deliverables) {
        this.deliverables = deliverables;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
