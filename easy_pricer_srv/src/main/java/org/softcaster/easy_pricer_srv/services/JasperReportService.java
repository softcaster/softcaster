/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.services;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.softcaster.core.dto.PositionProspectDto;

@Service
public class JasperReportService {

public byte[] exportToPdf(List<PositionProspectDto> data) throws JRException {
        // 1. Carica il template .jasper
        InputStream reportStream = getClass().getResourceAsStream("/reports/position_prospect.jasper");
        if (reportStream == null) {
            throw new IllegalArgumentException("Template di JasperReport non trovato!");
        }

        // 2. 🔥 TRASFORMAZIONE IN MAPPA: Scomponiamo i record Java in coppie chiave-valore pulite.
        // Questo bypassa i controlli sui metodi getter eliminando l'eccezione a runtime.
        List<Map<String, ?>> mappedData = new ArrayList<>();
        
        for (PositionProspectDto item : data) {
            Map<String, Object> map = new HashMap<>();
            // I nomi delle chiavi devono coincidere al 100% con i Name dei Fields in Studio
            map.put("positionCode", item.positionCode());
            map.put("assetCode", item.assetCode());
            map.put("assetDescription", item.assetDescription());
            map.put("counterpartyCode", item.counterpartyCode());
            map.put("totalQuantity", item.totalQuantity());
            map.put("averagePrice", item.averagePrice());
            map.put("marketPrice", item.marketPrice());
            map.put("marketValue", item.marketValue());
            map.put("realizedPnL", item.realizedPnL());
            map.put("unrealizedPnL", item.unrealizedPnL());
            
            mappedData.add(map);
        }

        // 3.PASSIAMO LA MAPPA AL MOTORE DI JASPER
        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(mappedData);

        // 4. Configurazione parametri standard
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "PROSPETTO POSIZIONI FINANZIARIE");

        // 5. Riempimento e compilazione finale
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
        
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
