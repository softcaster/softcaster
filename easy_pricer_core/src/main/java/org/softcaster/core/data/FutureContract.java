package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDate;
import java.util.Objects;
import org.hibernate.Hibernate;

/**
 * Singola scadenza di un future (6EZ6, MBTP dic-2026), dettaglio della testata
 * FutureMasterData (6E, MBTP).
 *
 * Una sottoclasse per tipo di future, simmetrica alle testate:
 * BondFutureContract (con paniere), FxFutureContract, MmFutureContract,
 * CmdFutureContract. Al salvataggio si verifica che la testata sia del tipo
 * corrispondente.
 *
 * Entity graph "FutureContract.withProduct": testata con valuta, calendario e
 * festivita', asset class e valutazione. Per i future su cambi la coppia
 * sottostante (con i calendari delle due valute) e' caricata da
 * FutureContractDAO.
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraph(
        name = "FutureContract.withProduct",
        attributeNodes = {
            @NamedAttributeNode(value = "masterData", subgraph = "productGraph")
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
public abstract class FutureContract extends ListedContract {

    private static final long serialVersionUID = 1L;

    /**
     * Lettere standard dei mesi di scadenza (F = gennaio ... Z = dicembre).
     */
    private static final String MONTH_CODES = "FGHJKMNQUVXZ";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "master_data", nullable = false)
    private FutureMasterData masterData;

    /**
     * Primo giorno del mese di scadenza.
     */
    @Column(name = "contract_month")
    private java.sql.Date contractMonth;

    @Column(name = "last_trading_date")
    private java.sql.Date lastTradingDate;

    /**
     * Data di consegna/regolamento, se nota. Null per i future regolati per
     * cassa la cui data valuta si calcola dall'ultimo giorno di negoziazione.
     */
    @Column(name = "delivery_date")
    private java.sql.Date deliveryDate;

    /**
     * Tipo di testata ammesso per questo tipo di contratto.
     *
     * @return
     */
    protected abstract Class<? extends FutureMasterData> productType();

    /**
     * La testata deve essere del tipo corrispondente al contratto (es. un
     * BondFutureContract sotto un BondFutureMasterData).
     */
    @PrePersist
    @PreUpdate
    private void checkProductType() {
        getMasterData(productType());
    }

    // ------------------------------------------------------------------------
    // -- Metodi di comodo
    // ------------------------------------------------------------------------
    /**
     * Testata con il tipo atteso, es. getMasterData(BondFutureMasterData.class).
     *
     * @param <T>
     * @param type
     * @return 
     * @throws IllegalArgumentException se la testata e' di un altro tipo
     */
    public <T extends FutureMasterData> T getMasterData(Class<T> type) {
        Objects.requireNonNull(type, "type");
        // con la testata caricata dall'entity graph non c'e' proxy; unproxy copre il caso LAZY
        Object product = Hibernate.unproxy(masterData);
        if (!type.isInstance(product)) {
            throw new IllegalArgumentException("Il contratto " + getCode() + " non e' un "
                    + type.getSimpleName() + " (testata " + product + ")");
        }
        return type.cast(product);
    }

    public LocalDate getContractMonthLocalDate() {
        return contractMonth == null ? null : contractMonth.toLocalDate();
    }

    public LocalDate getLastTradingLocalDate() {
        return lastTradingDate == null ? null : lastTradingDate.toLocalDate();
    }

    public LocalDate getDeliveryLocalDate() {
        return deliveryDate == null ? null : deliveryDate.toLocalDate();
    }

    /**
     * Imposta ultimo giorno di negoziazione e mese di scadenza (primo giorno
     * dello stesso mese).
     * @param lastTrading
     */
    public void setLastTradingLocalDate(LocalDate lastTrading) {
        Objects.requireNonNull(lastTrading, "lastTrading");
        this.lastTradingDate = java.sql.Date.valueOf(lastTrading);
        this.contractMonth = java.sql.Date.valueOf(lastTrading.withDayOfMonth(1));
    }

    /**
     * Codice standard del contratto: prodotto + lettera del mese + ultima cifra
     * dell'anno (6E, dicembre 2026 -> 6EZ6).
     * @param productCode
     * @param contractMonth
     * @return 
     */
    public static String standardCode(String productCode, LocalDate contractMonth) {
        Objects.requireNonNull(productCode, "productCode");
        Objects.requireNonNull(contractMonth, "contractMonth");
        return productCode + MONTH_CODES.charAt(contractMonth.getMonthValue() - 1) + (contractMonth.getYear() % 10);
    }

    // ------------------------------------------------------------------------
    // -- Getter / setter
    // ------------------------------------------------------------------------
    @Override
    public FutureMasterData getMasterData() {
        return masterData;
    }

    public void setMasterData(FutureMasterData masterData) {
        this.masterData = masterData;
    }

    public java.sql.Date getContractMonth() {
        return contractMonth;
    }

    public void setContractMonth(java.sql.Date contractMonth) {
        this.contractMonth = contractMonth;
    }

    public java.sql.Date getLastTradingDate() {
        return lastTradingDate;
    }

    public void setLastTradingDate(java.sql.Date lastTradingDate) {
        this.lastTradingDate = lastTradingDate;
    }

    public java.sql.Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(java.sql.Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
