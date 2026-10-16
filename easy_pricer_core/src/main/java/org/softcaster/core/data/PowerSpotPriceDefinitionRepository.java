/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PowerSpotPriceDefinitionRepository extends JpaRepository<PowerSpotPriceDefinition, Integer> {
    
    @Query("""
        SELECT psd
        FROM PowerSpotPriceDefinition psd
        WHERE psd.spotPriceDefinitionId = :id
        """)
    @EntityGraph("PowerSpotPriceDefinition.fullWithCountry")
    public PowerSpotPriceDefinition findByIdWithCountry(@Param("id") Integer id);

    @EntityGraph("PowerSpotPriceDefinition.fullWithCountry")
    @Override
    public List<PowerSpotPriceDefinition> findAll(Sort sortByCode);

    @Query("""
        SELECT psd
        FROM PowerSpotPriceDefinition psd
        WHERE psd.code = :code
        """)
    @EntityGraph("PowerSpotPriceDefinition.fullWithCountry")
    public PowerSpotPriceDefinition findByCodeWithCountry(@Param("code") String code);

    @Query("""
        SELECT psd
        FROM PowerSpotPriceDefinition psd
        WHERE psd.code = :code
        """)
    @EntityGraph("PowerSpotPriceDefinition.fullWithQuotes")
    public PowerSpotPriceDefinition findByCodeWithQuotes(@Param("code") String code);
}
