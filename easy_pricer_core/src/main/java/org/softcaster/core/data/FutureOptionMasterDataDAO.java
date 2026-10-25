package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serie delle opzioni su future (future_option_master_data).
 */
@Repository
public class FutureOptionMasterDataDAO extends AbstractMasterDataDAO<FutureOptionMasterData, FutureOptionMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public FutureOptionMasterDataDAO(FutureOptionMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Serie con testata e sottostante (finestre di anagrafica).
     */
    @Transactional(readOnly = true)
    @Override
    public List<FutureOptionMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    /**
     * Serie delle testate con le asset class indicate (FFO, FXO...).
     */
    @Transactional(readOnly = true)
    public List<FutureOptionMasterData> findAllByProductAssetClass(List<String> assetClasses) {
        return repository.findAllByProductAssetClass(assetClasses);
    }

    @Transactional(readOnly = true)
    public FutureOptionMasterData getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice opzione su future mancante");
        }
        return repository.findByCodeWithUnderlying(codeOrIsin)
                .or(() -> repository.findByIsinWithUnderlying(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Opzione su future non trovata: " + codeOrIsin));
    }
}