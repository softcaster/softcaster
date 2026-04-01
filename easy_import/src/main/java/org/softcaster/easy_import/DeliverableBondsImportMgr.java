/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_import.beans.Deliverable_bonds;
import org.softcaster.easy_import.beans.Deliverable_bondsDAO;
import org.softcaster.easy_import.beans.Master_data;
import org.softcaster.easy_import.beans.Master_dataDAO;

/**
 *
 * @author ep
 */
public class DeliverableBondsImportMgr implements IImportMgr {

    private static DeliverableBondsImportMgr _instance = null;

    private Master_data master_data = null;
    private Master_dataDAO master_dataDAO = null;
    private Deliverable_bonds deliverable_bonds = null;
    private Deliverable_bondsDAO deliverable_bondsDAO = null;

    private DeliverableBondsImportMgr() {
        createDAOs();
        createBeans();
    }

    private void createDAOs() {
        master_dataDAO = new Master_dataDAO();
        deliverable_bondsDAO = new Deliverable_bondsDAO();
    }

    private void createBeans() {
        deliverable_bonds = new Deliverable_bonds();
        master_data = new Master_data();
    }

    private int getIdMasterData(String isin) {
        int idMasterData = 0;
        master_data.setCode(isin);
        if (master_dataDAO.loadByIdx(master_data)) {
            idMasterData = master_data.getId_master_data();
        }

        return idMasterData;
    }

    private java.sql.Date decodeDate(String date) {

        java.sql.Date sqlDate = null;
        try {
            int year = Converter.toInt(date.substring(0,4));
            int month = Converter.toInt(date.substring(4,6));
            int day = Converter.toInt(date.substring(6,8));
            Date dt = new Date(year,month,day);
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
        config.setSeparator(';');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            int idMasterData = 0;
            for (String[] s : csvImport.getBuffer()) {
                if (s[1].isEmpty()) {
                    System.out.println("Error reading line ");
                    continue;
                }
                idMasterData = getIdMasterData(s[1].trim());
                if (idMasterData > 0) {
                    deliverable_bonds.setMaster_data(idMasterData);
                    deliverable_bonds.setExpiration_date(decodeDate(s[2].trim()));
                    deliverable_bonds.setIsin(s[3].trim());
                    deliverable_bonds.setCoupon_rate(Converter.toDouble(s[4].trim(), false));
                    deliverable_bonds.setBond_maturity(decodeDate(s[5].trim()));
                    deliverable_bonds.setBond_cf(Converter.toDouble(s[6].trim(), false));
                    deliverable_bondsDAO.insertOrUpdate(deliverable_bonds);
                }
            }

        } catch (Exception ex) {
            String error = "Error importing Future: " + deliverable_bonds.getIsin() + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
        if (master_dataDAO != null) {
            master_dataDAO.closeStatements();
        }
        if (deliverable_bondsDAO != null) {
            deliverable_bondsDAO.closeStatements();
        }
    }

    public static DeliverableBondsImportMgr getInstance() {
        if (_instance == null) {
            _instance = new DeliverableBondsImportMgr();
        }
        return _instance;
    }

}
