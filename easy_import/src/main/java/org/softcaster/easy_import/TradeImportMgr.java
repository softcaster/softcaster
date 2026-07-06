/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Counterparty;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.core.dto.FinancialTxnMapper;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author ep
 */
@Service("Trades")
public class TradeImportMgr implements IImportMgr {

    @Autowired
    private MasterDataDAO masterDataDAO;

    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;

    @Autowired
    private CounterpartyDAO counterpartyDAO;

    @Autowired
    private FinancialTxnMapper financialTxnMapper;

    private final WebClient webClient;

    public TradeImportMgr(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v1").build();
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/trades.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(1);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            List<String[]> rows = csvImport.getBuffer();
            int total = rows.size() - config.getStartData();
            int current = 1;
            for (String[] s : rows.subList(config.getStartData(), rows.size())) {
                if (s[0].isEmpty()) {
                    System.out.println("Error reading import file");
                    continue;
                }
                elabRow(s);
                // 2. Aggiorna il progresso ogni X righe o calcola la percentuale
                int percent = (int) ((current / (double) total) * 100);
                progressInfo.updateProgress("Importing " + s[0] + " (" + current + "/" + total + ")", percent);
                current++;
            }

        } catch (Exception ex) {
            String error = "Error during import: " + ex.getLocalizedMessage();;
            LoggerMgr.logError(error);
            progressInfo.showError(error);
        } finally {
            progressInfo.updateProgress("Import terminated successfully", 100);
            terminate();
        }

    }

    @Override
    public void terminate() {
    }

    private void elabRow(String[] s) {

        MasterData masterData = masterDataDAO.findByCode(s[0]);
        PositionMasterData position = positionMasterDataDAO.findByCode(s[1]);
        Counterparty counterparty = counterpartyDAO.findByCode(s[2]);

        FinancialTxn txn = new FinancialTxn();
        txn.setIdFinancialTxn(0);
        txn.setCounterparty(counterparty);
        txn.setDescription(s[7]);
        txn.setFxRate(1.);
        txn.setMasterData(masterData);
        txn.setPositionMd(position);
        try {
            txn.setQuantity(Converter.toDouble(s[3], false));
            txn.setPrice(Converter.toDouble(s[5], false));
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
        txn.setRefId(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.sql.Date tradeDate = java.sql.Date.valueOf(LocalDate.parse(s[6], formatter));
        txn.setTradeDate(tradeDate);
        txn.setSettlement(tradeDate);
        txn.setValueDate(tradeDate);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnSide(TxnSide.fromCode(s[4]));

        FinancialTxnDto txnDto = financialTxnMapper.toDto(txn);
        // .block() costringe WebClient a far partire la richiesta POST verso /financial_txn
        // Nel mondo reattivo, se non ti iscrivi (.subscribe() o .block()) al flusso, 
        // la richiesta HTTP non parte proprio
        saveFinancialTxn(txnDto).block();
    }

    public Mono<FinancialTxnDto> saveFinancialTxn(FinancialTxnDto financialTxn) {
        return this.webClient.post()
                .uri("/financial_txn")
                .bodyValue(financialTxn)
                .retrieve()
                .bodyToMono(FinancialTxnDto.class)
                .onErrorResume(error -> {
                    LoggerMgr.logError("Failed to save financial_txn: " + error.getLocalizedMessage());
                    return Mono.empty(); // Equivale a ritornare null/empty in modo reattivo
                });
    }
}
