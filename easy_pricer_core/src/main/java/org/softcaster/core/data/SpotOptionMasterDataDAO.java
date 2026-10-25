package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serie delle opzioni su strumento secco (spot_option_master_data).
 */
@Repository
public class SpotOptionMasterDataDAO extends AbstractMasterDataDAO<SpotOptionMasterData, SpotOptionMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public SpotOptionMasterDataDAO(SpotOptionMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Serie con testata e sottostante (finestre di anagrafica).
     */
    @Transactional(readOnly = true)
    @Override
    public List<SpotOptionMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    /**
     * Serie delle testate con le asset class indicate (FFO, FXO...).
     */
    @Transactional(readOnly = true)
    public List<SpotOptionMasterData> findAllByProductAssetClass(List<String> assetClasses) {
        return repository.findAllByProductAssetClass(assetClasses);
    }

    @Transactional(readOnly = true)
    public SpotOptionMasterData getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice opzione su strumento secco mancante");
        }
        return repository.findByCodeWithUnderlying(codeOrIsin)
                .or(() -> repository.findByIsinWithUnderlying(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Opzione su strumento secco non trovata: " + codeOrIsin));
    }
}