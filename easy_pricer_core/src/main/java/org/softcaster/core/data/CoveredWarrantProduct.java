package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Testata dei covered warrant di un emittente su un sottostante (es. "BNP CW
 * su SX5E"): opzioni plain vanilla cartolarizzate, quotate per warrant.
 *
 * ratio = multiplo, unita' di sottostante per warrant (0,01 = un centesimo di
 * indice): il prezzo del warrant e' il premio per unita' di sottostante x
 * ratio. La posizione e' in numero di warrant al prezzo del warrant: contract
 * value e multiplier della testata restano 1.
 */
@Entity
@Table(name = "covered_warrant_product")
@PrimaryKeyJoinColumn(name = "id_option_product")
@SuppressWarnings("PersistenceUnitPresent")
public class CoveredWarrantProduct extends SpotOptionProduct {

    private static final long serialVersionUID = 1L;

    public static final String ASSET_CLASS = "CWT";

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // letto dalle finestre fuori transazione
    @JoinColumn(name = "issuer", nullable = false)
    private Issuer issuer;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ratio", nullable = false)
    private Double ratio;

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }
}
