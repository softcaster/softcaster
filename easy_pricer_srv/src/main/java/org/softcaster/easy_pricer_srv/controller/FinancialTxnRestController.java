package org.softcaster.easy_pricer_srv.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.core.dto.FinancialTxnMapper;
import org.softcaster.easy_pricer_srv.services.FinancialTxnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinancialTxnRestController {

    @Autowired
    private FinancialTxnDAO dao;
    @Autowired
    FinancialTxnMapper mapper;
    @Autowired
    private FinancialTxnService financialTxnService;

    @GetMapping("/financial_txn/r01")
    public ResponseEntity<List<FinancialTxn>> findAll() {
        List<FinancialTxn> listaFinancialTxn = dao.findAll();
        if (listaFinancialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinancialTxn, HttpStatus.OK);
    }

    @GetMapping("/financial_txn/r10")
    public ResponseEntity<List<FinancialTxnDto>> findAllDto() {
        List<FinancialTxn> transactions = dao.findAll();
        if (transactions == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        // Conversione massiva in DTO
        List<FinancialTxnDto> dtoTransactions = transactions.stream()
                .map(entity -> mapper.toDto(entity))
                .toList();
        return new ResponseEntity(dtoTransactions, HttpStatus.OK);
    }

    @GetMapping("/financial_txn/r02/{id}")
    public ResponseEntity<FinancialTxn> findByIdFinancialTxn(@PathVariable("id") Integer idFinancialTxn) {
        FinancialTxn financialTxn = dao.findByIdFinancialTxn(idFinancialTxn);
        if (financialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(financialTxn, HttpStatus.OK);
    }

    @GetMapping("/financial_txn/r03/{code}")
    public ResponseEntity<List<FinancialTxnDto>> findAllByAssetClass(@PathVariable("code") String code) {
        List<FinancialTxn> transactions = dao.findAllByAssetClass(code);
        if (transactions == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        List<FinancialTxnDto> dtoTransactions = transactions.stream()
                .map(entity -> mapper.toDto(entity))
                .toList();
        return new ResponseEntity(dtoTransactions, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/financial_txn")
    public ResponseEntity<FinancialTxnDto> saveOrUpdate(@RequestBody FinancialTxnDto newTxnDto) {
        try {
            // Delega l'intera logica atomica al servizio
            FinancialTxnDto resultDto = financialTxnService.saveOrUpdateTransaction(newTxnDto);
            return new ResponseEntity<>(resultDto, HttpStatus.OK);
        } catch (Exception e) {
            // Se il service lancia una qualsiasi eccezione, la transazione fallisce,
            // viene eseguito il rollback automatico sul DB e restituisci l'errore al client.
            LoggerMgr.logError(e.getLocalizedMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/financial_txn/d01/{id}")
    public ResponseEntity<FinancialTxn> delete(@PathVariable("id") Integer idFinancialTxn) {
        FinancialTxn financialTxn = dao.findByIdFinancialTxn(idFinancialTxn);
        if (financialTxn != null) {
            dao.delete(financialTxn);
            return new ResponseEntity(financialTxn, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    // delete logica del record
    @DeleteMapping("/financial_txn/d02/{id}")
    public ResponseEntity<FinancialTxnDto> logicalDelete(@PathVariable("id") Integer idFinancialTxn) {
        try {
            // Delega l'intera logica atomica al servizio
            FinancialTxnDto resultDto = financialTxnService.logicalDelete(idFinancialTxn);
            return new ResponseEntity<>(resultDto, HttpStatus.OK);
        } catch (Exception e) {
            // Se il service lancia una qualsiasi eccezione, la transazione fallisce,
            // viene eseguito il rollback automatico sul DB e restituisci l'errore al client.
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/financial_txn/export/{assetClass}")
    public ResponseEntity<Resource> exportCsv(@PathVariable String assetClass) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            System.getLogger(FinancialTxnRestController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        // 1. Generi il contenuto del CSV (come stringa o array di byte)
        String csvContent = "ID;Currency;Price\n1;EURUSD;1.12\n2;GBPUSD;1.25";
        byte[] data = csvContent.getBytes(StandardCharsets.UTF_8);

        // 2. Crei la risorsa dai byte
        ByteArrayResource resource = new ByteArrayResource(data);

        // 3. Prepari gli headers per dire al browser "È un file da scaricare"
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export_" + assetClass + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .body(resource);
    }
}
