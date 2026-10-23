package org.softcaster.core.data;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO per le scadenze dei bond future: restituisce il contratto completo
 * (testata, valuta, calendario con festivita', paniere), utilizzabile fuori
 * dalla transazione.
 *
 * Le scritture passano da FutureContractDAO.saveOrUpdate (chiave naturale
 * testata + mese di scadenza, comune a tutti i future).
 */
@Component
@Transactional(readOnly = true)
public class BondFutureContractDAO {

    private final BondFutureContractRepository repository;

    public BondFutureContractDAO(BondFutureContractRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional(readOnly = true)
    public Optional<BondFutureContract> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<BondFutureContract> findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    /**
     * Contratto obbligatorio cercato per codice oppure per ISIN.
     *
     * @throws IllegalArgumentException se il valore e' vuoto
     * @throws NoSuchElementException se il bond future non esiste
     */
    @Transactional(readOnly = true)
    public BondFutureContract getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice bond future mancante");
        }
        return findByCode(codeOrIsin)
                .or(() -> findByIsin(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Bond future non trovato: " + codeOrIsin));
    }
}
