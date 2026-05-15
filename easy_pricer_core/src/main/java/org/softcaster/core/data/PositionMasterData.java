package org.softcaster.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "position_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class PositionMasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "position_master_data_seq", sequenceName = "position_master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "position_master_data_seq")
    @Column(name = "id_position", columnDefinition = "INTEGER")
    private Integer idPosition;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolio", nullable = true)
    private PortfolioMasterData portfolio;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
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

    /**
     * @return the portfolio
     */
    public PortfolioMasterData getPortfolio() {
        return portfolio;
    }

    /**
     * @param portfolio the portfolio to set
     */
    public void setPortfolio(PortfolioMasterData portfolio) {
        this.portfolio = portfolio;
    }
}
