package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contratti accumulator / decumulator (accumulator_master_data).
 */
@Repository
public class AccumulatorMasterDataDAO extends AbstractMasterDataDAO<AccumulatorMasterData, AccumulatorMasterDataRepository> {

    public AccumulatorMasterDataDAO(AccumulatorMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Nuovo contratto con il suo calendario dei fixing. Il codice contiene
     * l'id, che la sequence assegna al salvataggio: si salva con un codice
     * provvisorio e si aggiorna nella stessa transazione.
     */
    @Transactional
    public AccumulatorMasterData create(AccumulatorMasterData amd) {
        if (amd.getIdMasterData() != null) {
            throw new IllegalArgumentException("Contract " + amd.getCode() + " already saved");
        }
        if (amd.getFixings().isEmpty()) {
            throw new IllegalArgumentException("Empty fixing schedule");
        }
        amd.setCode("NEW-" + UUID.randomUUID().toString().substring(0, 18));
        AccumulatorMasterData saved = repository.saveAndFlush(amd);
        saved.setCode(saved.contractCode());
        return saved;
    }

    @Transactional(readOnly = true)
    public AccumulatorMasterData getByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Missing accumulator code");
        }
        return repository.findByCodeWithFixings(code)
                .orElseThrow(() -> new NoSuchElementException("Accumulator not found: " + code));
    }

    @Transactional(readOnly = true)
    public AccumulatorMasterData getById(Integer id) {
        return repository.findByIdWithFixings(id)
                .orElseThrow(() -> new NoSuchElementException("Accumulator not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<AccumulatorMasterData> findAllByAssetClasses(List<String> assetClasses) {
        return repository.findAllByAssetClasses(assetClasses);
    }
}
