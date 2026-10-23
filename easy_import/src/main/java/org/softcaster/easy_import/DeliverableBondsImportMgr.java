/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.BondFutureContract;
import org.softcaster.core.data.BondFutureContractDAO;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.DeliverableBonds;
import org.softcaster.core.data.FutureContract;
import org.softcaster.core.data.FutureContractDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Import del paniere consegnabile dei bond future da deliverable_bonds.csv.
 *
 * Colonne (separatore ';', prima riga di intestazione): [0] codice testata
 * (es. MBTP) [1] ISIN della scadenza del future [2] ultimo giorno di
 * negoziazione (yyyyMMdd) [3] ISIN titolo [4] cedola [5] scadenza titolo
 * (yyyyMMdd) [6] fattore di conversione
 *
 * Le righe sono raggruppate per scadenza (ISIN del future). Le scadenze di
 * testate non presenti in anagrafica sono ignorate; la scadenza
 * (BondFutureContract) viene creata se manca. Ogni scadenza
 * e' caricata una volta sola (con il paniere, tramite entity graph) e
 * aggiornata in una propria transazione: un errore su una scadenza non blocca
 * l'import delle altre.
 */
@Service("Deliverables")
public class DeliverableBondsImportMgr implements IImportMgr {

    private static final int COL_PRODUCT = 0;
    private static final int COL_FUTURE = 1;
    private static final int COL_EXPIRY = 2;
    private static final int COL_ISIN = 3;
    private static final int COL_COUPON = 4;
    private static final int COL_MATURITY = 5;
    private static final int COL_CF = 6;
    private static final int MIN_COLUMNS = 7;

    /**
     * true: il file contiene il paniere completo, i titoli non presenti nel
     * file vengono rimossi. false: si aggiungono solo i titoli nuovi, quelli
     * esistenti restano invariati.
     */
    private static final boolean REPLACE_BASKET = false;

    private final BondFutureMasterDataDAO bondFutureMasterDataDAO;
    private final BondFutureContractDAO bondFutureContractDAO;
    private final FutureContractDAO futureContractDAO;
    private final TransactionTemplate transactionTemplate;

    public DeliverableBondsImportMgr(BondFutureMasterDataDAO bondFutureMasterDataDAO,
            BondFutureContractDAO bondFutureContractDAO,
            FutureContractDAO futureContractDAO,
            PlatformTransactionManager transactionManager) {
        this.bondFutureMasterDataDAO = bondFutureMasterDataDAO;
        this.bondFutureContractDAO = bondFutureContractDAO;
        this.futureContractDAO = futureContractDAO;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        Path path = Paths.get(IMPORT_PATH + "/deliverable_bonds.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(';');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8);

        try {
            CsvImport csvImport = new CsvImport();
            csvImport.startImport(config);
            Map<String, List<String[]>> rowsByFuture = groupByFuture(csvImport.getBuffer());

            int total = rowsByFuture.size();
            int current = 0;
            List<String> errors = new ArrayList<>();

            for (Map.Entry<String, List<String[]>> entry : rowsByFuture.entrySet()) {
                String futureCode = entry.getKey();
                current++;
                progressInfo.updateProgress("Importing " + futureCode + " (" + current + "/" + total + ")",
                        (int) (current * 100.0 / total));
                try {
                    // una transazione per future: la sessione resta aperta mentre si modifica il paniere
                    transactionTemplate.executeWithoutResult(status
                            -> importFuture(futureCode, entry.getValue()));
                } catch (RuntimeException ex) {
                    String error = futureCode + ": " + ex.getLocalizedMessage();
                    LoggerMgr.logError("Error importing deliverables of " + error);
                    errors.add(error);
                }
            }

            if (errors.isEmpty()) {
                progressInfo.updateProgress("Import terminated successfully", 100);
            } else {
                progressInfo.showError("Import terminated with " + errors.size() + " errors: " + errors);
            }

        } catch (Exception ex) {
            String error = "Error importing deliverable bonds [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            progressInfo.showError(error);
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    // ------------------------------------------------------------------------
    // -- Import di un singolo future (eseguito dentro la transazione)
    // ------------------------------------------------------------------------
    private void importFuture(String futureIsin, List<String[]> rows) {
        BondFutureContract contract = loadOrCreateContract(futureIsin, rows.get(0));
        if (contract == null) {
            return;
        }

        if (REPLACE_BASKET) {
            contract.getDeliverables().clear();      // orphanRemoval cancella le righe vecchie
        }

        Set<String> existing = new HashSet<>();
        contract.getDeliverables().forEach(d -> existing.add(d.getIsin()));

        for (String[] s : rows) {
            try {
                String isin = s[COL_ISIN].trim();
                if (!existing.add(isin)) {
                    continue;                            // gia' nel paniere
                }
                DeliverableBonds deliverable = new DeliverableBonds();
                // la FK listed_contract e' valorizzata da Hibernate tramite il @JoinColumn del contratto
                deliverable.setIsin(isin);
                deliverable.setBondCf(Converter.toDouble(s[COL_CF].trim(), false));
                deliverable.setExpirationDate(decodeDate(s[COL_EXPIRY].trim()));
                deliverable.setCouponRate(Converter.toDouble(s[COL_COUPON].trim(), false));
                deliverable.setBondMaturity(decodeDate(s[COL_MATURITY].trim()));
                contract.getDeliverables().add(deliverable);
            }
            // nessun save esplicito: l'entity e' gestita e le modifiche vengono scritte al commit
            catch (ParseException ex) {
                System.getLogger(DeliverableBondsImportMgr.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }

    /**
     * Scadenza del future per ISIN; se manca la crea sotto la testata indicata
     * nella colonna [0], con codice standard (MBTPZ6) e ultimo giorno di
     * negoziazione dalla colonna [2].
     *
     * @return null se la testata non e' in anagrafica (future non gestito)
     */
    private BondFutureContract loadOrCreateContract(String futureIsin, String[] row) {
        BondFutureContract existing = bondFutureContractDAO.findByIsin(futureIsin).orElse(null);
        if (existing != null) {
            return existing;
        }

        String productCode = row[COL_PRODUCT].trim();
        BondFutureMasterData bfmd = bondFutureMasterDataDAO.findByCode(productCode);
        if (bfmd == null) {
            return null;                                 // testata non gestita: si passa alla successiva
        }

        LocalDate lastTrading = decodeDate(row[COL_EXPIRY].trim()).toLocalDate();
        BondFutureContract contract = new BondFutureContract();
        contract.setMasterData(bfmd);
        contract.setIsin(futureIsin);
        contract.setLastTradingLocalDate(lastTrading);
        contract.setCode(FutureContract.standardCode(productCode, lastTrading));
        return futureContractDAO.saveOrUpdate(contract);
    }

    // ------------------------------------------------------------------------
    // -- Helper
    // ------------------------------------------------------------------------
    /**
     * Salta l'intestazione e le righe incomplete, raggruppa per codice future
     * mantenendo l'ordine del file.
     */
    private static Map<String, List<String[]>> groupByFuture(List<String[]> rows) {
        Map<String, List<String[]>> result = new LinkedHashMap<>();
        for (int i = 1; i < rows.size(); i++) {      // riga 0 = intestazione
            String[] s = rows.get(i);
            if (s.length < MIN_COLUMNS || s[COL_FUTURE].isBlank() || s[COL_ISIN].isBlank()) {
                LoggerMgr.logError("deliverable_bonds.csv: riga " + (i + 1) + " incompleta, ignorata");
                continue;
            }
            result.computeIfAbsent(s[COL_FUTURE].trim(), k -> new ArrayList<>()).add(s);
        }
        return result;
    }

    /**
     * Data nel formato yyyyMMdd; un valore non valido interrompe l'import del
     * future.
     */
    private static java.sql.Date decodeDate(String date) {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("data non valida '" + date + "'", ex);
        }
    }
}
