package org.softcaster.core.data;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("masterDataDAO")
    public class MasterDataDAO {

    private final MasterDataRepository repository;

    // Iniezione tramite costruttore (Best Practice per Spring)
    public MasterDataDAO(MasterDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public MasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional(readOnly = true)
    public Optional<MasterData> findByIdWithInstrumentValuation(Integer id) {
        return repository.findByIdWithInstrumentValuation(id);
    }

    @Transactional(readOnly = true)
    public MasterData findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<MasterData> findByCodeWithInstrumentValuation(String code) {
        return repository.findByCodeWithInstrumentValuation(code);
    }

    @Transactional
    public MasterData saveOrUpdate(MasterData masterData) {
        // Gestione di sicurezza per la relazione OneToOne simulata:
        // Se nel DB esiste già una valutazione per questo strumento, dobbiamo assicurarci 
        // che Hibernate non tenti di inserire un duplicato a causa della lista interna.
        InstrumentValuation currentValuation = masterData.getInstrumentValuation();
        if (currentValuation != null && masterData.getIdMasterData() != null) {
            // Se stiamo aggiornando un MasterData esistente, forziamo il corretto 
            // legame logico prima del flush di Hibernate
            masterData.setInstrumentValuation(currentValuation);
        }

        return repository.save(masterData);
    }

    @Transactional
    public void delete(MasterData masterData) {
        repository.delete(masterData);
    }

    @Transactional(readOnly = true)
    public List<MasterData> findAll() {
        return repository.findAll();
    }

}
