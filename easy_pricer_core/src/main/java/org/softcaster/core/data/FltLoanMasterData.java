package org.softcaster.core.data;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;

/**
 * Mutuo a tasso variabile (FRM): condizioni del variabile in
 * FloatingRateTerms, come per i titoli. interest_rate del livello cash flow
 * resta il tasso della prima rata (o l'ultimo fissato).
 */
@Entity
@Table(name = "flt_loan_master_data")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "FltLoanMasterData.fullGraph",
        attributeNodes = {
            @NamedAttributeNode(value = "currency", subgraph = "currencyGraph"),
            @NamedAttributeNode("assetClass"),
            @NamedAttributeNode("instrumentValuation"),
            @NamedAttributeNode("cashFlows"),
            @NamedAttributeNode(value = "floatingTerms", subgraph = "floatingTerms")
        },
        subgraphs = {
            @NamedSubgraph(name = "currencyGraph", attributeNodes = @NamedAttributeNode("calendar")),
            @NamedSubgraph(name = "floatingTerms", attributeNodes = @NamedAttributeNode("projectionIndex"))
        }
)
public class FltLoanMasterData extends LoanMasterData {

    @Embedded
    private FloatingRateTerms floatingTerms = new FloatingRateTerms();

    public FloatingRateTerms getFloatingTerms() {
        if (floatingTerms == null) {
            floatingTerms = new FloatingRateTerms();
        }
        return floatingTerms;
    }

    public void setFloatingTerms(FloatingRateTerms floatingTerms) {
        this.floatingTerms = floatingTerms;
    }
}
