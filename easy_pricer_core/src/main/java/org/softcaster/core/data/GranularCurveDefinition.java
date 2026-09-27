/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(
        name = "granular_curve_definition",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_granular_curve",
                    columnNames = {
                        "market_quote_definition_id",
                        "shape_profile_id"
                    }
            )
        }
)
public class GranularCurveDefinition implements Serializable {

    @Id
    @SequenceGenerator(
            name = "granular_curve_definition_seq",
            sequenceName = "granular_curve_definition_s",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "granular_curve_definition_seq"
    )
    @Column(name = "granular_curve_id")
    private Integer granularCurveId;

    @Column(
            name = "market_quote_definition_id",
            nullable = false
    )
    private Integer marketQuoteDefinition;

    @Column(
            name = "shape_profile_id",
            nullable = false
    )
    private Integer shapeProfile;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(
            name = "granular_curve_id",
            nullable = false
    )
    private List<GranularCurvePoint> points
            = new ArrayList<>();

    // getter / setter

    /**
     * @return the granularCurveId
     */
    public Integer getGranularCurveId() {
        return granularCurveId;
    }

    /**
     * @param granularCurveId the granularCurveId to set
     */
    public void setGranularCurveId(Integer granularCurveId) {
        this.granularCurveId = granularCurveId;
    }

    /**
     * @return the marketQuoteDefinition
     */
    public Integer getMarketQuoteDefinition() {
        return marketQuoteDefinition;
    }

    /**
     * @param marketQuoteDefinition the marketQuoteDefinition to set
     */
    public void setMarketQuoteDefinition(Integer marketQuoteDefinition) {
        this.marketQuoteDefinition = marketQuoteDefinition;
    }

    /**
     * @return the shapeProfile
     */
    public Integer getShapeProfile() {
        return shapeProfile;
    }

    /**
     * @param shapeProfile the shapeProfile to set
     */
    public void setShapeProfile(Integer shapeProfile) {
        this.shapeProfile = shapeProfile;
    }

    /**
     * @return the points
     */
    public List<GranularCurvePoint> getPoints() {
        return points;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(List<GranularCurvePoint> points) {
        this.points = points;
    }
}
