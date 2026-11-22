package org.softcaster.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.BondValuationConverter;
import org.softcaster.core.data.converters.PositionTypeConverter;
import org.softcaster.engine.enums.BondValuation;
import org.softcaster.engine.enums.PositionType;

@Entity
@Table(name = "position_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class PositionMasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "position_master_data_seq", sequenceName = "position_master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "position_master_data_seq")
    @Column(name = "id_position", columnDefinition = "INTEGER")
    private Integer idPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    // nodo padre nella gerarchia (null per le radici), come gl_accounts.parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", nullable = true)
    private PositionMasterData parent;

    // livello del nodo; solo POSITION e' postable
    @Convert(converter = PositionTypeConverter.class)
    @Column(name = "position_type", nullable = false)
    private PositionType positionType = PositionType.POSITION;

    // true se il nodo puo' essere valorizzato, cioe' alimentare position_detail
    @Column(name = "is_postable", nullable = false)
    private boolean postable = true;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    // spot FX della posizione rollati a ogni chiusura invece di regolare a T+2
    @Column(name = "fx_rolling", nullable = false)
    private boolean fxRolling = false;

    // criterio di valutazione dei titoli della posizione
    @Convert(converter = BondValuationConverter.class)
    @Column(name = "bond_valuation", nullable = false)
    private BondValuation bondValuation = BondValuation.FAIR_VALUE;

    @OneToMany(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "position_md") // FK in child table position_detail
    private List<PositionDetail> details = new ArrayList<>();

    public Integer getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(Integer idPosition) {
        this.idPosition = idPosition;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFxRolling() {
        return fxRolling;
    }

    public void setFxRolling(boolean fxRolling) {
        this.fxRolling = fxRolling;
    }

    public BondValuation getBondValuation() {
        return bondValuation;
    }

    public void setBondValuation(BondValuation bondValuation) {
        this.bondValuation = bondValuation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdPosition() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PositionMasterData that = (PositionMasterData) obj;
        return getIdPosition().equals(that.getIdPosition());
    }

    @Override
    public int hashCode() {
        return getIdPosition() == null ? 0 : idPosition.hashCode();
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the details
     */
    public List<PositionDetail> getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(List<PositionDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return code;
    }

    public PositionMasterData getParent() {
        return parent;
    }

    public void setParent(PositionMasterData parent) {
        this.parent = parent;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    /**
     * Imposta il tipo e allinea is_postable (vincolo ck_position_postable).
     */
    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
        this.postable = positionType != null && positionType.isPostable();
    }

    public boolean isPostable() {
        return postable;
    }

    public void setPostable(boolean postable) {
        this.postable = postable;
    }
}
