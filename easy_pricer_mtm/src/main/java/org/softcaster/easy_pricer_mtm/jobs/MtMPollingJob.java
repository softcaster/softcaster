/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.easy_pricer_mtm.services.EodMtmService;
import org.softcaster.easy_pricer_mtm.services.MtmService;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    private EodMtmService eodMtmService;

    // asset class contabilizzate a MTM di fine giornata (script con l'evento MTM)
    @Value("#{'${mtm.eod.asset-classes:XRB,XRN,FRB,FRN,BFU,FFU,FSP}'.split(',')}")
    private Set<String> eodAssetClasses;

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

    private void refreshSpotPrices() {
        marketDataService.loadSpotPrice();
    }

    private void refreshDiscountCurves() {
        marketDataService.reloadCurves();
    }

    // Chiama rest per un Mtm on-flight
    public boolean executeJob() {
        ValuationContext context = new ValuationContext();

        // Aggiornamento tassi/prezzi
        refreshSpotPrices();

        // Aggiornamento curve
        refreshDiscountCurves();
        
        // Valutazione posizioni
        fetchPosition(context);

        // SALVATAGGIO FINALE DELLE VALUTAZIONI (Esterno al ciclo delle posizioni)
        // tutte le valutazioni: un errore su uno strumento non ferma le altre
        return saveValuations(context);
    }

    /**
     * MtM di fine giornata: valuta tutte le posizioni (non solo quelle non
     * ricalcolate da 15 secondi, come il polling), salva le valutazioni e
     * scrive la fotografia per la contabilita' (EodMtmService) delle posizioni
     * valutate senza errori, alla data ufficiale. Da lanciare dopo il
     * caricamento dei prezzi di chiusura.
     */
    public EodTaskResult executeEod() {
        ValuationContext context = new ValuationContext();
        // la data ufficiale e' in cache (businessCalendar): il fine giornata la rilegge
        marketDataService.refreshOfficialDate();
        refreshSpotPrices();
        refreshDiscountCurves();
        LocalDate valuationDate = getOfficialDate();

        List<PositionDetail> details = positionDetailDAO.findAll();
        Set<Integer> failed = new HashSet<>();
        for (PositionDetail detail : details) {
            try {
                elabPosition(detail, context);
            } catch (Exception e) {
                failed.add(detail.getIdPositionDetail());
                log.error("### EOD MTM ERROR FOR POSITION DETAIL ID {}: {}", detail.getIdPositionDetail(), e.getMessage());
            }
        }
        boolean valuationsSaved = saveValuations(context);

        Map<EodMtmService.Outcome, Integer> counts = new EnumMap<>(EodMtmService.Outcome.class);
        int snapshotErrors = 0;
        for (PositionDetail detail : details) {
            if (failed.contains(detail.getIdPositionDetail())) {
                continue;
            }
            try {
                counts.merge(eodMtmService.recordSnapshot(detail.getIdPositionDetail(), valuationDate, eodAssetClasses), 1, Integer::sum);
            } catch (Exception e) {
                snapshotErrors++;
                log.error("### EOD MTM SNAPSHOT ERROR FOR POSITION DETAIL ID {}: {}", detail.getIdPositionDetail(), e.getMessage());
            }
        }
        int snapshotsCreated = counts.getOrDefault(EodMtmService.Outcome.CREATED, 0);
        int snapshotsUpdated = counts.getOrDefault(EodMtmService.Outcome.UPDATED, 0);
        int alreadyPosted = counts.getOrDefault(EodMtmService.Outcome.ALREADY_POSTED, 0);
        int unchanged = counts.getOrDefault(EodMtmService.Outcome.UNCHANGED, 0);
        int notAccounted = counts.getOrDefault(EodMtmService.Outcome.NOT_ACCOUNTED, 0);
        String message = "Snapshots: " + snapshotsCreated + " new, " + snapshotsUpdated + " updated, "
                + alreadyPosted + " already posted, " + unchanged + " unchanged, " + notAccounted + " not accounted at MTM; "
                + failed.size() + " valuation errors, " + snapshotErrors + " snapshot errors"
                + (valuationsSaved ? "" : "; some instrument valuations not saved");
        EodTaskResult result = EodTaskResult.of(EodStep.MTM, valuationDate, details.size(), snapshotsCreated + snapshotsUpdated,
                alreadyPosted + unchanged + notAccounted, failed.size() + snapshotErrors + (valuationsSaved ? 0 : 1), message);
        // una fotografia gia' contabilizzata non si aggiorna: i prezzi rivisti dopo non entrano in contabilita'
        if (result.status() == EodStatus.OK && alreadyPosted > 0) {
            result = result.asWarning(message);
        }
        log.info("=== [MMS] EOD MTM {} ===", result);
        return result;
    }

    /**
     * Upsert di tutte le valutazioni del ciclo; false se almeno una non e'
     * stata salvata.
     */
    private boolean saveValuations(ValuationContext context) {
        boolean allSaved = true;
        Collection<InstrumentValuation> valuationsToSave = context.getValuationCache().values();
        for (InstrumentValuation valuation : valuationsToSave) {
            try {
                // Sfruttare l'Upsert nativo (ON CONFLICT DO UPDATE) a fine ciclo è la scelta vincente
                // in questo tipo di architetture asincrone.
                // azzera i problemi causati dai proxy di Hibernate nel multithreading,
                // garantisce prestazioni elevate e mantiene il database perfettamente ordinato,
                // con gli ID delle valutazioni saldamente ancorati a quelli delle anagrafiche
                instrumentValuationDAO.upsertValuation(valuation);
            } catch (Exception e) {
                log.error("### MTM error saving valuation instrument Id {}: {}",
                        valuation.getMasterData().getIdMasterData(), e.getMessage());
                allSaved = false;
            }
        }
        return allSaved;
    }

    @Scheduled(fixedDelay = 60000)
    public void pollPositionDetails() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [MSRV] Service is suspended ===\n");
            return;
        }
        log.info("=== [MMS] MTM Service started ===");
        // Creazione del contesto unico per QUESTO ciclo di polling
        ValuationContext context = new ValuationContext();

        // Aggiornamento tassi/prezzi
        refreshSpotPrices();
        
        // Aggiornamento curve
        refreshDiscountCurves();

        // Valutazione posizioni
        fetchPosition(context);

        // SALVATAGGIO FINALE DELLE VALUTAZIONI (Esterno al ciclo delle posizioni)
        saveValuations(context);
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
