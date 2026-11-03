package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Evento contabile riferito a una posizione e non a una transazione (ratei,
 * cedole, MTM): dati comuni alle foglie. Nessuna tabella propria: le colonne
 * stanno nella tabella di ogni foglia.
 *
 * accountingNominal: nominale della posizione con segno (negativo = posizione
 * corta), senza multiplier; chi lo usa per un importo moltiplica.
 */
@MappedSuperclass
@SuppressWarnings("PersistenceUnitPresent")
public abstract class PositionAccountingEvent extends AccountingEvent {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "accounting_nominal")
    private Double accountingNominal;

    public Double getAccountingNominal() {
        return accountingNominal;
    }

    public void setAccountingNominal(Double accountingNominal) {
        this.accountingNominal = accountingNominal;
    }
}
