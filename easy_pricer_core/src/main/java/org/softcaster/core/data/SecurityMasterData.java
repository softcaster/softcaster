package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.ArrayList;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "security_master_data")
@SuppressWarnings("PersistenceUnitPresent")

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "SecurityMasterData.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass"),
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("issuer"),}
    ),
    @NamedEntityGraph(
            name = "SecurityMasterData.withCashFlow",
            attributeNodes = {
                @NamedAttributeNode("cashFlows"),
                @NamedAttributeNode(
                        value = "currency",
                        subgraph = "currencyGraph"
                )
            },
            subgraphs = {
                @NamedSubgraph(
                        name = "currencyGraph",
                        attributeNodes = {
                            @NamedAttributeNode("calendar")
                        }
                )
            }
    )
})
public class SecurityMasterData extends MasterData {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer", nullable = true)
    private Issuer issuer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "master_data", nullable = false) // FK in child table cash_flow_item
    private List<CashFlowItem> cashFlows = new ArrayList<>();

    @Column(name = "isin")
    private String isin;

    @Column(name = "cfi_code")
    private String cfiCode;

    @Column(name = "fisn")
    private String fisn;

    @Column(name = "lei")
    private String lei;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "nominal_value")
    private Double nominalValue;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "first_coupon_rate")
    private Double firstCouponRate;

    @Column(name = "first_coupon_payment_date")
    private java.sql.Date firstCouponPaymentDate;

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCfiCode() {
        return cfiCode;
    }

    public void setCfiCode(String cfiCode) {
        this.cfiCode = cfiCode;
    }

    public String getFisn() {
        return fisn;
    }

    public void setFisn(String fisn) {
        this.fisn = fisn;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public Double getNominalValue() {
        return nominalValue;
    }

    public void setNominalValue(Double nominalValue) {
        this.nominalValue = nominalValue;
    }

    public Double getFirstCouponRate() {
        return firstCouponRate;
    }

    public void setFirstCouponRate(Double firstCouponRate) {
        this.firstCouponRate = firstCouponRate;
    }

    public java.sql.Date getFirstCouponPaymentDate() {
        return firstCouponPaymentDate;
    }

    public void setFirstCouponPaymentDate(java.sql.Date firstCouponPaymentDate) {
        this.firstCouponPaymentDate = firstCouponPaymentDate;
    }

    /**
     * @return the issuer
     */
    public Issuer getIssuer() {
        return issuer;
    }

    /**
     * @param issuer the issuer to set
     */
    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    @Override
    public String toString() {
        return isin;
    }

    /**
     * @return the cashFlows
     */
    public List<CashFlowItem> getCashFlows() {
        return cashFlows;
    }

    /**
     * @param cashFlows the cashFlows to set
     */
    public void setCashFlows(List<CashFlowItem> cashFlows) {
        this.cashFlows = cashFlows;
    }
}
