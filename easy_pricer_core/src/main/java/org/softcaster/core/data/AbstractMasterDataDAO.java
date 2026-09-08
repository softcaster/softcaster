/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.sql.Date;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.dto.MasterDataDto;

public abstract class AbstractMasterDataDAO<E extends MasterData2, R extends BaseMasterDataRepository<E>> {

    protected final R repository;

    protected AbstractMasterDataDAO(R repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public E findByIdMasterData(Integer idMasterData) {
        // Il cast a (E) è sicuro grazie ai vincoli dei Generics sui Repository
        return (E) repository.findByIdMasterData(idMasterData);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByIdWithInstrumentValuation(Integer id) {
        return repository.findByIdWithInstrumentValuation(id).map(entity -> (E) entity);
    }

    @Transactional(readOnly = true)
    public E findByCode(String code) {
        return (E) repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByCodeWithInstrumentValuation(String code) {
        return repository.findByCodeWithInstrumentValuation(code).map(entity -> (E) entity);
    }

    @Transactional
    public E saveOrUpdate(E masterData) {
        // Gestione di sicurezza centralizzata per la relazione OneToOne
        InstrumentValuation currentValuation = masterData.getInstrumentValuation();
        if (currentValuation != null && masterData.getIdMasterData() != null) {
            masterData.setInstrumentValuation(currentValuation);
        }
        return repository.save(masterData);
    }

    @Transactional
    public void delete(E masterData) {
        repository.delete(masterData);
    }

    @Transactional(readOnly = true)
    public List<E> findAll() {
        return (List<E>) repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<E> findByCriteria(String code, Date maturityLessEq, Date maturityGreatEq) {
        // Nota: Assicurati che MasterDataSpecifications restituisca una Specification<E> compatibile
        Specification<E> spec = (Specification<E>) MasterDataSpecifications2.withFilters(code, maturityLessEq, maturityGreatEq);
        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<E> findAllByAssetClass(String code) {
        return (List<E>) repository.findAllByAssetClass(code);
    }

    @Transactional(readOnly = true)
    public List<MasterDataDto> findAllDtoByAssetClass(String code) {
        List<E> mdList = findAllByAssetClass(code);
        List<MasterDataDto> mdDtoList = null;
        if (mdList != null && !mdList.isEmpty()) {
            mdDtoList = new ArrayList<>();
            for (E md : mdList) {
                MasterDataDto dto = new MasterDataDto();
                dto.setGenericMasterDataId(md.getIdMasterData());
                dto.setCode(md.getCode());
                dto.setDescription(md.getDescription());
                mdDtoList.add(dto);
            }
        }
        return mdDtoList;
    }
}
