/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.DeliverableBonds;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("Deliverables")
public class DeliverableBondsImportMgr implements IImportMgr {

    @Autowired
    MasterDataDAO masterDataDAO;
    @Autowired
    BondFutureMasterDataDAO bondFutureMasterDataDAO;

    public DeliverableBondsImportMgr() {
    }

    private java.sql.Date decodeDate(String date) {

        java.sql.Date sqlDate = null;
        try {
            int year = Converter.toInt(date.substring(0, 4));
            int month = Converter.toInt(date.substring(4, 6));
            int day = Converter.toInt(date.substring(6, 8));
            Date dt = new Date(year, month, day);
            return dt.sqlDate();
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
        return sqlDate;
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/deliverable_bonds.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            List<String[]> rows = csvImport.getBuffer();
            int total = rows.size();
            int current = 0;

            int idMasterData = 0;
            for (String[] s : rows) {
                if (s[1].isEmpty()) {
                    System.out.println("Error reading line ");
                    continue;
                }

                MasterData md = masterDataDAO.findByCode(s[1].trim());
                if (md instanceof BondFutureMasterData bfmd) {
                    DeliverableBonds deliverable = new DeliverableBonds();
                    deliverable.setMasterData(idMasterData);
                    deliverable.setBondCf(Converter.toDouble(s[6].trim(), false));
                    deliverable.setExpirationDate(decodeDate(s[2].trim()));
                    deliverable.setIsin(s[3].trim());
                    deliverable.setCouponRate(Converter.toDouble(s[4].trim(), false));
                    deliverable.setBondMaturity(decodeDate(s[5].trim()));
                    bfmd.getDeliverables().add(deliverable);
                    bondFutureMasterDataDAO.saveOrUpdate(bfmd);
                }

                // 2. Aggiorna il progresso ogni X righe o calcola la percentuale
                int percent = (int) ((current / (double) total) * 100);
                progressInfo.updateProgress("Importing " + s[3].trim() + " (" + current + "/" + total + ")", percent);
                current++;
            }

        } catch (Exception ex) {
            String error = "Error importing Future: " + " [" + ex.getLocalizedMessage() + "]";
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

}
