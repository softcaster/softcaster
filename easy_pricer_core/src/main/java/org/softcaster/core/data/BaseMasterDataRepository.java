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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

@NoRepositoryBean // Dice a Spring di non istanziare questo bean
public interface BaseMasterDataRepository<T extends MasterData2> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {

    // 1. Caricamento Standard (Lazy): Restituisce l'entità pulita
    T findByIdMasterData(Integer idMasterData);

    T findByCode(String code);

    // 2. Caricamento Completo (Eager con EntityGraph): Restituisce l'Optional con il Join Fetch
    @EntityGraph(value = "MasterData.fullGraph")
    Optional<T> findByIdWithInstrumentValuation(Integer idMasterData);

    @EntityGraph(value = "MasterData.fullGraph")
    Optional<T> findByCodeWithInstrumentValuation(String code);

    // 3. Altri metodi comuni
    @Query("SELECT md FROM MasterData2 md WHERE md.assetClass.code = :code ORDER BY md.maturityDate ASC")
    List<T> findAllByAssetClass(String code);
}
