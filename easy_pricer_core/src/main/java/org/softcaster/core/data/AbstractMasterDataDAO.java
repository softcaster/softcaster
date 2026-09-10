/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.sql.Date;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.dto.MasterDataDto;

public abstract class AbstractMasterDataDAO<
        E extends MasterData, R extends BaseMasterDataRepository<E>> {

    protected final R repository;

    protected AbstractMasterDataDAO(R repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public E findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByIdWithInstrumentValuation(Integer id) {
        return repository.findByIdWithInstrumentValuation(id);
    }

    @Transactional(readOnly = true)
    public E findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<E> findByCodeWithInstrumentValuation(String code) {
        return repository.findByCodeWithInstrumentValuation(code);
    }

    @Transactional
    public E saveOrUpdate(E masterData) {
        return repository.save(masterData);
    }

    @Transactional
    public void delete(E masterData) {
        repository.delete(masterData);
    }

    @Transactional(readOnly = true)
    public List<E> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<E> findByCriteria(
            String code,
            Date maturityLessEq,
            Date maturityGreatEq) {

        Specification<E> spec
                = MasterDataSpecifications.withFilters(
                        code,
                        maturityLessEq,
                        maturityGreatEq);

        return repository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<E> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    @Transactional(readOnly = true)
    public List<MasterDataDto> findAllDtoByAssetClass(String code) {

        List<E> mdList = findAllByAssetClass(code);

        if (mdList == null || mdList.isEmpty()) {
            return Collections.emptyList();
        }

        List<MasterDataDto> result = new ArrayList<>();

        for (E md : mdList) {
            MasterDataDto dto = new MasterDataDto();

            dto.setGenericMasterDataId(md.getIdMasterData());
            dto.setCode(md.getCode());
            dto.setDescription(md.getDescription());

            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<E> findByDescriptionContaining(String description) {
        return repository.findByDescriptionContaining(description);
    }
}
