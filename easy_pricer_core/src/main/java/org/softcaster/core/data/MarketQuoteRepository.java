/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.softcaster.engine.enums.LoadType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketQuoteRepository extends JpaRepository<MarketQuote, Integer> {

    Optional<MarketQuote> findByBusinessDateAndMasterDataAndLoadType(
        LocalDate businessDate, CmdFutureMasterData masterData, LoadType loadType
    );

    List<MarketQuote> findByBusinessDateAndMarketDataMarket(
        LocalDate businessDate, String market
    );
}