package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import org.hibernate.Hibernate;

/**
 * Singola scadenza di un future (MBTPZ6, M6EZ6): lo strumento negoziabile,
 * riga di master_data. I dati comuni a tutte le scadenze stanno nella testata
 * (FutureProduct), a cui ogni sottoclasse punta con il tipo corretto
 * (BondFutureMasterData -> BondFutureProduct, ...).
 *
 * I getter dei dati di testata (getContractValue, getInitialMargin, ...)
 * delegano a getProduct(): processori, contabilita' e MtM li usano senza
 * conoscere la testata.
 */
@Entity
@Table(name = "future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class FutureMasterData extends MasterData {

    @Column(name = "isin")
    private String isin;

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

    // ------------------------------------------------------------------------
    // -- Testata
    // ------------------------------------------------------------------------
    /**
     * Testata della scadenza.
     *
     * @return
     */
    public abstract FutureProduct getProduct();

    /**
     * Tipo di testata ammesso per questo tipo di scadenza.
     *
     * @return
     */
    protected abstract Class<? extends FutureProduct> productType();

    /**
     * La testata deve esistere ed essere del tipo corrispondente alla scadenza.
     */
    @PrePersist
    @PreUpdate
    private void checkProduct() {
        Object product = Hibernate.unproxy(getProduct());
        if (!productType().isInstance(product)) {
            throw new IllegalStateException("Scadenza " + getCode() + ": testata " + product
                    + " non e' un " + productType().getSimpleName());
        }
    }

    /**
     * Copia nella scadenza i valori di default della testata (valuta, asset
     * class, giorni di regolamento, multiplier).
     *
     * @param product
     */
    protected void copyProductDefaults(FutureProduct product) {
        setCurrency(product.getCurrency());
        setAssetClass(product.getAssetClass());
        setBusinessDays(product.getBusinessDays());
        setMultiplier(product.getMultiplier());
    }

    // dati di testata, per chi usa la scadenza
    public String getExchangeContractCode() {
        return getProduct().getExchangeContractCode();
    }

    public org.softcaster.engine.enums.SettlementType getSettlementType() {
        return getProduct().getSettlementType();
    }

    public Double getContractValue() {
        return getProduct().getContractValue();
    }

    public Double getTickSize() {
        return getProduct().getTickSize();
    }

    public Double getInitialMargin() {
        return getProduct().getInitialMargin();
    }

    public Double getMaintenanceMargin() {
        return getProduct().getMaintenanceMargin();
    }

    // ------------------------------------------------------------------------
    // -- Date
    // ------------------------------------------------------------------------
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
     *
     * @param lastTrading
     */
    public void setLastTradingLocalDate(LocalDate lastTrading) {
        Objects.requireNonNull(lastTrading, "lastTrading");
        this.lastTradingDate = java.sql.Date.valueOf(lastTrading);
        this.contractMonth = java.sql.Date.valueOf(lastTrading.withDayOfMonth(1));
    }

    // ------------------------------------------------------------------------
    // -- Getter / setter
    // ------------------------------------------------------------------------
    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
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
