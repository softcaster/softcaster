package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FltSecurityMasterDataDAO extends AbstractMasterDataDAO<FltSecurityMasterData, FltSecurityMasterDataRepository> {

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    public FltSecurityMasterDataDAO(
            FltSecurityMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Titoli con emittente, indice di riferimento e flussi (finestre di
     * anagrafica).
     */
    @Transactional(readOnly = true)
    @Override
    public List<FltSecurityMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    public Optional<FltSecurityMasterData> findByIdWithRefRateIndex(Integer id) {
        return repository.findByIdWithRefRateIndex(id);
    }

    public FltSecurityMasterData findByCodeWithCashFlow(String code) {
        return repository.findByCodeWithCashFlow(code);
    }
}
