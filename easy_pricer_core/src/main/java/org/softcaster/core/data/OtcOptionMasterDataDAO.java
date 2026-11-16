package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Opzioni OTC (OFX, OCM): un contratto per anagrafica, creato dal deal
 * capture con il codice definitivo assegnato dopo il primo salvataggio.
 */
@Repository
public class OtcOptionMasterDataDAO extends AbstractMasterDataDAO<OtcOptionMasterData, OtcOptionMasterDataRepository> {

    public OtcOptionMasterDataDAO(OtcOptionMasterDataRepository repository) {
        super(repository);
    }

    @Transactional
    public OtcOptionMasterData create(OtcOptionMasterData omd) {
        if (omd.getIdMasterData() != null) {
            throw new IllegalArgumentException("Contract " + omd.getCode() + " already saved");
        }
        omd.setCode("NEW-" + UUID.randomUUID().toString().substring(0, 18));
        OtcOptionMasterData saved = repository.saveAndFlush(omd);
        saved.setCode(saved.contractCode());
        return saved;
    }

    @Transactional(readOnly = true)
    public OtcOptionMasterData getByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Missing option code");
        }
        return repository.findOtcByCode(code)
                .orElseThrow(() -> new NoSuchElementException("OTC option not found: " + code));
    }

    @Transactional(readOnly = true)
    public OtcOptionMasterData getById(Integer id) {
        return repository.findOtcById(id)
                .orElseThrow(() -> new NoSuchElementException("OTC option not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OtcOptionMasterData> findAllByAssetClasses(List<String> assetClasses) {
        return repository.findAllByAssetClasses(assetClasses);
    }
}
