/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.marketdataprovider.OFFSET_TYPE;
import org.softcaster.marketdataprovider.YieldNode;

/**
 *
 * @author ep
 */
public class EcbYcImportMgr implements IImportMgr {

    private static EcbYcImportMgr _instance = null;

    // Statico per essere usato dal Singleton
    private static YieldCurveDAO yieldCurveDAO;

    private final List<YieldNode> nodes = new ArrayList<>();

    private EcbYcImportMgr() {
        buildNodes();
    }

    private YieldNode getNode(String ric) {
        for (YieldNode node : nodes) {
            if (node.getRic().equals(ric)) {
                return node;
            }
        }

        return null;
    }

    private void buildNodes() {
        YieldNode node = null;

        // Tassi mensili
        node = new YieldNode();
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(3);
        node.setRic("SR_3M");
        nodes.add(node);

        node = new YieldNode();
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(6);
        node.setRic("SR_6M");
        nodes.add(node);

        node = new YieldNode();
        node.setOffsetType(OFFSET_TYPE.MOUNTHS);
        node.setOffset(9);
        node.setRic("SR_9M");
        nodes.add(node);

        // Tassi annuali
        for (int cnt = 1; cnt <= 30; cnt++) {
            node = new YieldNode();
            node.setOffsetType(OFFSET_TYPE.YEARS);
            node.setOffset(cnt);
            node.setRic("SR_" + cnt + "Y");
            nodes.add(node);
        }
    }


    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/data.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            YieldNode node = null;
            for (String[] s : csvImport.getBuffer()) {
                if (!s[5].equals("G_N_C")) {
                    continue;
                }
                node = getNode(s[7]);
                if (node != null) {
                    double value = Double.parseDouble(s[9].trim());
                    node.setAsk(value);
                    node.setBid(value);
                }
            }
                YieldCurveImportMgr.saveNodes(nodes,"ECBYC",yieldCurveDAO);
        } catch (Exception ex) {
            String error = "Error importing";
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    public static EcbYcImportMgr getInstance(YieldCurveDAO dao) {
        if (_instance == null) {
            _instance = new EcbYcImportMgr();
            yieldCurveDAO = dao;
        }
        return _instance;
    }
    
    @Override
    public String getImportInfo() {
        return "ECBYC";
    }
}
