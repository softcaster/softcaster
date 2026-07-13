/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.account.JournalEntriesDAO;
import org.softcaster.core.dto.AccountDetailsBalanceDto;
import org.softcaster.core.dto.PositionProspectDto;
import org.softcaster.core.dto.ProspectFilter;
import org.softcaster.easy_pricer_srv.services.JasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProspectsRestController {

    @Autowired
    PositionDetailDAO positionDetailDAO;
    
    @Autowired
    JournalEntriesDAO journalEntriesDAO;

    @Autowired
    private JasperReportService jasperReportService; // Il servizio che creeremo sotto

    @PostMapping(value = "/prospects/position")
    public ResponseEntity<List<PositionProspectDto>> getPositionProspect(@RequestBody ProspectFilter filter) {
        if (filter.getAssetClassId() == null && filter.getCounterpartyId() == null && filter.getPositionId() == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        List<PositionProspectDto> ppList = positionDetailDAO.getPositionProspect(filter.getPositionId(), filter.getCounterpartyId(), filter.getAssetClassId());
        if (ppList == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(ppList, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/prospects/position/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPositionProspectPdf(@RequestBody ProspectFilter filter) {
        if (filter.getAssetClassId() == null && filter.getCounterpartyId() == null && filter.getPositionId() == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        try {
            // 1. Recupera la stessa lista di DTO che usala griglia web
            List<PositionProspectDto> dataList = positionDetailDAO.getPositionProspect(
                    filter.getPositionId(),
                    filter.getCounterpartyId(),
                    filter.getAssetClassId()
            );

            // 2. Genera il file PDF in memoria (array di byte)
            byte[] pdfBytes = jasperReportService.exportToPdf(dataList);

            // 3. Prepara gli header HTTP per forzare il download o l'apertura nel browser
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "position_prospect.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            LoggerMgr.logError(e.getLocalizedMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/prospects/accounting/r01/{id}")
    public ResponseEntity findBalanceWithDetailsByPositionDetail(@PathVariable("id") Integer idPositionDetail) {
        List<AccountDetailsBalanceDto>  listAccountDetailsBalance = journalEntriesDAO.findBalanceWithDetailsByPositionDetail(idPositionDetail);
        if (listAccountDetailsBalance == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listAccountDetailsBalance, HttpStatus.OK);
    }

    @PostMapping(value = "/prospects/accounting/r02")
    public ResponseEntity<List<AccountDetailsBalanceDto>> getAccountingProspect(@RequestBody ProspectFilter filter) {
        if (filter.getAssetClassId() == null && filter.getCounterpartyId() == null && filter.getPositionId() == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        List<AccountDetailsBalanceDto>  listAccountDetailsBalance = journalEntriesDAO.findBalanceWithDetailsByPositionDetail(1);
        if (listAccountDetailsBalance == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(listAccountDetailsBalance, HttpStatus.OK);
        }
    }
    
}
