package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serie delle opzioni (option_master_data).
 */
@Repository
public class OptionMasterDataDAO extends AbstractMasterDataDAO<OptionMasterData, OptionMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public OptionMasterDataDAO(OptionMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Serie con testata e sottostante (finestre di anagrafica).
     */
    @Transactional(readOnly = true)
    @Override
    public List<OptionMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    /**
     * Serie delle testate con le asset class indicate (FFO, FXO...).
     */
    @Transactional(readOnly = true)
    public List<OptionMasterData> findAllByProductAssetClass(List<String> assetClasses) {
        return repository.findAllByProductAssetClass(assetClasses);
    }

    @Transactional(readOnly = true)
    public OptionMasterData getByCodeOrIsin(String codeOrIsin) {
        if (codeOrIsin == null || codeOrIsin.isBlank()) {
            throw new IllegalArgumentException("Codice opzione mancante");
        }
        return repository.findByCodeWithUnderlying(codeOrIsin)
                .or(() -> repository.findByIsinWithUnderlying(codeOrIsin))
                .orElseThrow(() -> new NoSuchElementException("Opzione non trovata: " + codeOrIsin));
    }
}
