package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_master_data")
@SuppressWarnings("PersistenceUnitPresent")

public class PortfolioMasterData implements Serializable {

    @Id
    @SequenceGenerator(name = "portfolio_master_data_seq", sequenceName = "portfolio_master_data_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "portfolio_master_data_seq")
    @Column(name = "id_portfolio", columnDefinition = "INTEGER")
    private Integer idPortfolio;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdPortfolio() {
        return idPortfolio;
    }

    public void setIdPortfolio(Integer idPortfolio) {
        this.idPortfolio = idPortfolio;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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
        if (getIdPortfolio() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PortfolioMasterData that = (PortfolioMasterData) obj;
        return getIdPortfolio().equals(that.getIdPortfolio());
    }

    @Override
    public int hashCode() {
        return getIdPortfolio() == null ? 0 : idPortfolio.hashCode();
    }
    
    @Override
    public String toString() {
        return code;
    }
}
