package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FxFutureMasterDataDAO extends AbstractMasterDataDAO<FxFutureMasterData, FxFutureMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public FxFutureMasterDataDAO(FxFutureMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FxFutureMasterData> findAll() {
        return repository.findAll(sortByCode);
    }
    
    @Transactional(readOnly = true)
    public Optional<FxFutureMasterData> findByCodeWithUnderlying(String code) {
        return repository.findByCodeWithUnderlying(code);
    }

    /**
     * Scadenza obbligatoria cercata per codice (M6EZ6) oppure per ISIN, con
     * testata, coppia sottostante e calendari.
     *
     * @param codeOrIsin
     * @return
     * @throws IllegalArgumentException se il valore e' vuoto
     * @throws java.util.NoSuchElementException se la scadenza non esiste
     */
    @Transactional(readOnly = true)
    public FxFutureMasterData getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice future su cambi mancante");
        }
        return repository.findByCodeWithUnderlying(codeOrIsin)
                .or(() -> repository.findByIsinWithUnderlying(codeOrIsin))
                .orElseThrow(() -> new java.util.NoSuchElementException("Future su cambi non trovato: " + codeOrIsin));
    }
}
