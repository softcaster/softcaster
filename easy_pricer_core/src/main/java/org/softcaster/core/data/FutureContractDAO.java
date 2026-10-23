package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO per le scadenze dei future.
 *
 * I metodi di lettura restituiscono il contratto con testata, valuta e
 * calendario con festivita': utilizzabile fuori dalla transazione. Per il
 * paniere dei bond future: BondFutureContractDAO.
 * Per i future su cambi viene caricata anche la coppia sottostante con i
 * calendari delle due valute (getCurrencyList() della testata).
 *
 * saveOrUpdate lavora sulla chiave naturale (testata, mese di scadenza).
 */
@Component
@Transactional(readOnly = true)
public class FutureContractDAO {

    private final FutureContractRepository repository;

    public FutureContractDAO(FutureContractRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    // ------------------------------------------------------------------------
    // -- Letture
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public Optional<FutureContract> findByCode(String code) {
        return repository.findByCode(code).map(FutureContractDAO::initializeProduct);
    }

    @Transactional(readOnly = true)
    public Optional<FutureContract> findByIsin(String isin) {
        return repository.findByIsin(isin).map(FutureContractDAO::initializeProduct);
    }

    /**
     * Come findByCode, ma il contratto e' obbligatorio.
     *
     * @param code
     * @return 
     * @throws NoSuchElementException se il contratto non esiste
     */
    @Transactional(readOnly = true)
    public FutureContract get(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Codice contratto future mancante");
        }
        return findByCode(code).orElseThrow(() -> new NoSuchElementException("Contratto future non trovato: " + code));
    }

    /**
     * Contratto obbligatorio cercato per codice (6EZ6) oppure per ISIN: i
     * chiamanti (REST, MtM) passano l'uno o l'altro.
     *
     * @param codeOrIsin
     * @return 
     * @throws IllegalArgumentException se il valore e' vuoto
     * @throws NoSuchElementException se il contratto non esiste
     */
    @Transactional(readOnly = true)
    public FutureContract getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice contratto future mancante");
        }
        return findByCode(codeOrIsin)
                .or(() -> findByIsin(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Contratto future non trovato: " + codeOrIsin));
    }

    /**
     * Contratto della testata con il mese di scadenza indicato (qualunque
     * giorno del mese).
     * @param idMasterData
     * @param contractMonth
     * @return 
     */
    @Transactional(readOnly = true)
    public Optional<FutureContract> find(Integer idMasterData, LocalDate contractMonth) {
        Objects.requireNonNull(contractMonth, "contractMonth");
        return repository.findByMasterDataAndContractMonth(idMasterData,
                java.sql.Date.valueOf(contractMonth.withDayOfMonth(1)))
                .map(FutureContractDAO::initializeProduct);
    }

    /**
     * Scadenze di una testata, ordinate per ultimo giorno di negoziazione
     * (senza paniere e senza calendario).
     * @param idMasterData
     * @return 
     */
    @Transactional(readOnly = true)
    public List<FutureContract> findByMasterData(Integer idMasterData) {
        return repository.findByMasterData(idMasterData);
    }

    // ------------------------------------------------------------------------
    // -- Scritture
    // ------------------------------------------------------------------------
    /**
     * Crea o aggiorna il contratto identificato da (testata, mese di
     * scadenza), per qualunque tipo di future (FutureContract,
     * BondFutureContract). Se esiste gia' si aggiornano codice, ISIN e date;
     * il paniere di un bond future resta quello esistente.
     *
     * @param <T>
     * @param contract
     * @return 
     * @throws IllegalArgumentException se il contratto esistente e' di un
     * altro tipo
     */
    @Transactional
    public <T extends FutureContract> T saveOrUpdate(T contract) {
        Objects.requireNonNull(contract, "contract");
        Objects.requireNonNull(contract.getMasterData(), "masterData");
        Objects.requireNonNull(contract.getContractMonth(), "contractMonth");
        FutureContract existing = repository.findByMasterDataAndContractMonth(
                contract.getMasterData().getIdMasterData(), contract.getContractMonth())
                .orElse(null);
        if (existing == null) {
            return repository.save(contract);
        }
        Object target = Hibernate.unproxy(existing);
        if (target.getClass() != contract.getClass()) {
            throw new IllegalArgumentException("Il contratto " + existing.getCode() + " esiste gia' come "
                    + target.getClass().getSimpleName() + ", non come " + contract.getClass().getSimpleName());
        }
        @SuppressWarnings("unchecked")
        T typed = (T) target;
        typed.setCode(contract.getCode());
        typed.setIsin(contract.getIsin());
        typed.setLastTradingDate(contract.getLastTradingDate());
        typed.setDeliveryDate(contract.getDeliveryDate());
        return repository.save(typed);
    }

    @Transactional
    public void delete(FutureContract contract) {
        repository.delete(contract);
    }

    // ------------------------------------------------------------------------
    // -- Helper
    // ------------------------------------------------------------------------
    /**
     * Completa la testata con i dati non coperti dall'entity graph: per un
     * future su cambi la coppia sottostante e i calendari di entrambe le
     * valute.
     */
    private static FutureContract initializeProduct(FutureContract contract) {
        Object product = Hibernate.unproxy(contract.getMasterData());
        if (product instanceof FxFutureMasterData fx && fx.getUnderlying() != null) {
            ForexMasterData pair = fx.getUnderlying();
            Hibernate.initialize(pair);
            initializeCalendar(pair.getBcy());
            initializeCalendar(pair.getCcy());
        }
        return contract;
    }

    private static void initializeCalendar(Currency currency) {
        if (currency != null) {
            Hibernate.initialize(currency.getCalendar());
            Hibernate.initialize(currency.getCalendar().getHolidays());
        }
    }
}
