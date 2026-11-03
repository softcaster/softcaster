package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Evento MTM di fine giornata di una posizione (position_detail): fotografia
 * della posizione alla data ufficiale, scritta dal MtM di end of day
 * (easy_pricer_mtm). La contabilita' registra la variazione rispetto
 * all'ultima fotografia gia' contabilizzata della stessa posizione: margine
 * di variazione per i future (realizzato + non realizzato), utile / perdita
 * non realizzati per i titoli (solo non realizzato).
 *
 * Importi nella settlement currency, prezzi come sulla posizione.
 */
@Entity
@Table(name = "accounting_event_mtm")
@SuppressWarnings("PersistenceUnitPresent")
public class AccountingEventMtm extends PositionAccountingEvent {

    @Column(name = "valuation_date")
    private LocalDate valuationDate;

    // prezzo di mercato quotato (base 100 per i titoli, secco)
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "market_price")
    private Double marketPrice;

    // prezzo medio unitario del lato aperto (0 se la posizione e' chiusa)
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "average_price")
    private Double averagePrice;

    // P&L realizzato cumulato della posizione
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "realized_pnl")
    private Double realizedPnl;

    // P&L non realizzato della quantita' netta al prezzo di mercato
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "unrealized_pnl")
    private Double unrealizedPnl;

    /**
     * Risultato complessivo della posizione: realizzato + non realizzato.
     */
    public double totalPnl() {
        return (realizedPnl == null ? 0. : realizedPnl) + (unrealizedPnl == null ? 0. : unrealizedPnl);
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Double getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(Double realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public Double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(Double unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }
}
