package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BondFutureMasterDataDAO extends AbstractMasterDataDAO<BondFutureMasterData, BondFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public BondFutureMasterDataDAO(BondFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BondFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    /**
     * Scadenza con testata, calendario e paniere.
     *
     * @param isin
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<BondFutureMasterData> findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    /**
     * Scadenza obbligatoria cercata per codice (MBTPZ6) oppure per ISIN, con
     * testata, calendario e paniere.
     *
     * @param codeOrIsin
     * @return
     * @throws IllegalArgumentException se il valore e' vuoto
     * @throws NoSuchElementException se la scadenza non esiste
     */
    @Transactional(readOnly = true)
    public BondFutureMasterData getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice bond future mancante");
        }
        return repository.findByCodeWithDeliverables(codeOrIsin)
                .or(() -> repository.findByIsin(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Bond future non trovato: " + codeOrIsin));
    }

    @Transactional(readOnly = true)
    public Optional<BondFutureMasterData> findByProductAndContractMonth(BondFutureProduct product,
            java.time.LocalDate contractMonth) {
        return repository.findByProductAndContractMonth(product.getIdFutureProduct(),
                java.sql.Date.valueOf(contractMonth.withDayOfMonth(1)));
    }
}
