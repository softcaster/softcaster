/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.Optional;
import java.util.List;

@NoRepositoryBean // Dice a Spring di non istanziare questo repository direttamente
public interface BaseMasterDataRepository<T extends MasterData> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {

    T findByIdMasterData(Integer idMasterData);

    T findByCode(String code);

    Optional<T> findByIdWithInstrumentValuation(Integer id);

    Optional<T> findByCodeWithInstrumentValuation(String code);

    List<T> findAllByAssetClass(String code);
}
