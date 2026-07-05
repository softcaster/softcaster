/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.InstrumentValuationDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.services.EngineStateManager;
import org.softcaster.easy_pricer_mtm.services.MtmService;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MtMPollingJob implements IMtmDataHelper {

    private static final Logger log = LoggerFactory.getLogger(MtMPollingJob.class);

    @Autowired
    private EngineStateManager engineStateManager;
    @Autowired
    private MtmService mtmService;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private InstrumentValuationDAO instrumentValuationDAO;

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    // Iniezione del pool di thread 
    @Autowired
    @Qualifier("mtmExecutor")
    private TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        // Aggiorna dati di mercato
        marketDataService.loadSpotPrice();
    }

    private void elabPosition(PositionDetail detail, ValuationContext context) {
        Integer positionMdId = detail.getPositionMd();
        Integer masterDataId = detail.getMasterData();
        Integer counterpartyId = detail.getCounterparty();

        // ogni position viene elaborata e committata singolarmente
        mtmService.evaluatePosition(positionMdId, masterDataId, counterpartyId, this, context);
    }

    private void fetchPositionDetails(PositionMasterData pmd, ValuationContext context) {
        // Chiediamo al DAO le righe che non vengono calcolate da almeno 15 secondi
        List<PositionDetail> details = positionDetailDAO.fetchAndClaimByPositionMasterData(pmd, 15).orElse(null);

        if (details != null && !details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        elabPosition(detail, context);
                    } catch (Exception e) {
                        log.error("### MTM ERROR FOR POSITION DETAIL ID {}: {}", detail.getIdPositionDetail(), e.getMessage());
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }

    private void fetchPosition(ValuationContext context) {
        List<PositionMasterData> positions = positionMasterDataDAO.findAll();

        if (!positions.isEmpty()) {
            for (PositionMasterData pmd : positions) {
                fetchPositionDetails(pmd, context);
            }
        }        
    }

    @Scheduled(fixedDelay = 15000)
    public void pollPositionDetails() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [MMS] MTM Service is suspended ===\n");
            return;
        }
        log.info("=== [MMS] MTM Service started ===");
        // Creazione del contesto unico per QUESTO ciclo di polling
        ValuationContext context = new ValuationContext();
        
        // Valutazione posizioni
        fetchPosition(context);
        
        // SALVATAGGIO FINALE DELLE VALUTAZIONI (Esterno al ciclo delle posizioni)
        Collection<InstrumentValuation> valuationsToSave = context.getValuationCache().values();
        if (!valuationsToSave.isEmpty()) {
            for (InstrumentValuation valuation : valuationsToSave) {
                try {
                    // Sfruttare l'Upsert nativo (ON CONFLICT DO UPDATE) a fine ciclo è la scelta vincente 
                    // in questo tipo di architetture asincrone.
                    // azzera i problemi causati dai proxy di Hibernate nel multithreading, 
                    // garantisce prestazioni elevate e mantiene il database perfettamente ordinato, 
                    // con gli ID delle valutazioni saldamente ancorati a quelli delle anagrafiche
                    instrumentValuationDAO.upsertValuation(valuation);             
                } 
                catch (Exception e) {
                    log.error("### MTM error saving valuation instrument Id {}: {}", 
                        valuation.getMasterData().getIdMasterData(), e.getMessage());
                }
            }
        }        
        log.info("=== [MMS] MTM Service terminated ===\n");
    }

    @Override
    public double getSpotPrice(String ticker, RequestType request) {
        return marketDataService.getSpotPrice(ticker, request);
    }

    @Override
    public LocalDate getOfficialDate() {
        return marketDataService.getOfficialDate();
    }

    @Override
    public double getSpotPrice(Integer masterDataId, RequestType request) {
        return marketDataService.getSpotPrice(masterDataId, request);
    }
}
