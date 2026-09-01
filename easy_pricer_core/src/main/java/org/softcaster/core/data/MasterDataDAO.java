package org.softcaster.core.data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.dto.MasterDataDto;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional(readOnly = true)
    public List<MasterData> findByCriteria(String code, Date maturityLessEq, Date maturityGreatEq) {
        Specification<MasterData> spec = MasterDataSpecifications.withFilters(code, maturityLessEq, maturityGreatEq);
        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<MasterData> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    public List<MasterDataDto> findAllDtoByAssetClass(String code) {
        List<MasterData> mdList = findAllByAssetClass(code);
        List<MasterDataDto> mdDtoList = null;
        if (mdList != null && !mdList.isEmpty()) {
            mdDtoList = new ArrayList<>();
            MasterDataDto dto;
            for(MasterData md: mdList) {
                dto = new MasterDataDto();
                dto.setGenericMasterDataId(md.getIdMasterData());
                dto.setCode(md.getCode());
                dto.setDescription(md.getDescription());
                mdDtoList.add(dto);
            }
        }
        return mdDtoList;
    }
}
