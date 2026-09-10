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
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface BaseMasterDataRepository<E extends MasterData>
        extends JpaRepository<E, Integer>,
        JpaSpecificationExecutor<E> {

    @Query("""
        SELECT md
        FROM MasterData md
        WHERE md.idMasterData = :id
        """)
    @EntityGraph("MasterData.fullGraph")
    E findByIdMasterData(@Param("id") Integer idMasterData);

    @Query("""
        SELECT md
        FROM MasterData md
        WHERE md.code = :code
        """)
    @EntityGraph("MasterData.fullGraph")
    E findByCode(@Param("code") String code);

    @Query("""
        SELECT md
        FROM MasterData md
        WHERE md.idMasterData = :id
        """)
    @EntityGraph("MasterData.valuationGraph")
    Optional<E> findByIdWithInstrumentValuation(
            @Param("id") Integer id);

    @Query("""
        SELECT md
        FROM MasterData md
        WHERE md.code = :code
        """)
    @EntityGraph("MasterData.valuationGraph")
    Optional<E> findByCodeWithInstrumentValuation(
            @Param("code") String code);

    @Query("""
        SELECT md
        FROM MasterData md
        WHERE md.assetClass.code = :code
        ORDER BY md.maturityDate ASC
        """)
    List<E> findAllByAssetClass(
            @Param("code") String code);

    List<E> findByDescriptionContaining(String description);
}
