package org.softcaster.core.data;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import org.softcaster.engine.enums.CouponProjectionMethod;
import org.softcaster.engine.enums.FixingDayType;

/**
 * Titolo a tasso variabile: le condizioni del variabile sono in
 * FloatingRateTerms (condivisa con i mutui); i getter qui sotto delegano, per
 * chi usa il titolo.
 */
@Entity
@Table(name = "flt_security_master_data")
@SuppressWarnings("PersistenceUnitPresent")

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "FltSecurityMasterData.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass"),
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("issuer"),
            }),
    @NamedEntityGraph(
            name = "FltSecurityMasterData.indexGraph",
            attributeNodes = {
                @NamedAttributeNode(value = "floatingTerms", subgraph = "floatingTerms")
            },
            subgraphs = {
                @NamedSubgraph(name = "floatingTerms", attributeNodes = @NamedAttributeNode("projectionIndex"))
            }),
    // lista delle finestre di anagrafica: tutto cio' che la finestra del titolo legge
    // fuori dalla transazione (un'unica collezione: i flussi)
    @NamedEntityGraph(
            name = "FltSecurityMasterData.fullGraphWithCashFlow",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass"),
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("issuer"),
                @NamedAttributeNode(value = "floatingTerms", subgraph = "floatingTerms"),
                @NamedAttributeNode("cashFlows")
            },
            subgraphs = {
                @NamedSubgraph(name = "floatingTerms", attributeNodes = @NamedAttributeNode("projectionIndex"))
            })
})
public class FltSecurityMasterData extends SecurityMasterData {

    @Embedded
    private FloatingRateTerms floatingTerms = new FloatingRateTerms();

    public FloatingRateTerms getFloatingTerms() {
        return floatingTerms;
    }

    public void setFloatingTerms(FloatingRateTerms floatingTerms) {
        this.floatingTerms = floatingTerms;
    }

    private FloatingRateTerms terms() {
        // Hibernate lascia null un embeddable con tutte le colonne vuote
        if (floatingTerms == null) {
            floatingTerms = new FloatingRateTerms();
        }
        return floatingTerms;
    }

    public Double getSpread() {
        return terms().getSpread();
    }

    public void setSpread(Double spread) {
        terms().setSpread(spread);
    }

    public CouponProjectionMethod getCouponPm() {
        return terms().getCouponPm();
    }

    public void setCouponPm(CouponProjectionMethod couponPm) {
        terms().setCouponPm(couponPm);
    }

    public FixingDayType getFixingDayType() {
        return terms().getFixingDayType();
    }

    public void setFixingDayType(FixingDayType fixingDayType) {
        terms().setFixingDayType(fixingDayType);
    }

    public Double getFloor() {
        return terms().getFloor();
    }

    public void setFloor(Double floor) {
        terms().setFloor(floor);
    }

    public Double getCap() {
        return terms().getCap();
    }

    public void setCap(Double cap) {
        terms().setCap(cap);
    }

    public RefRateIndex getProjectionIndex() {
        return terms().getProjectionIndex();
    }

    public void setProjectionIndex(RefRateIndex projectionIndex) {
        terms().setProjectionIndex(projectionIndex);
    }

    public Integer getFixingLagDays() {
        return terms().getFixingLagDays();
    }

    public void setFixingLagDays(Integer fixingLagDays) {
        terms().setFixingLagDays(fixingLagDays);
    }

    public Integer getIndexMonths() {
        return terms().getIndexMonths();
    }

    public void setIndexMonths(Integer indexMonths) {
        terms().setIndexMonths(indexMonths);
    }

    public Integer getProjectionCurveId() {
        return terms().getProjectionCurveId();
    }

    public void setProjectionCurveId(Integer projectionCurveId) {
        terms().setProjectionCurveId(projectionCurveId);
    }
}
