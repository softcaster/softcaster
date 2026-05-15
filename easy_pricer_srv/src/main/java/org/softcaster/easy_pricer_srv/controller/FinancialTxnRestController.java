package org.softcaster.easy_pricer_srv.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.TxnStatusDAO;
import org.softcaster.easy_pricer_srv.util.CommonData;
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
    private TxnStatusDAO txnStatusDAO;

    @GetMapping("/financial_txn/r01")
    public ResponseEntity<List<FinancialTxn>> findAll() {
        List<FinancialTxn> listaFinancialTxn = dao.findAll();
        if (listaFinancialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinancialTxn, HttpStatus.OK);
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
    public ResponseEntity<List<FinancialTxn>> findAllByAssetClass(@PathVariable("code") String code) {
        List<FinancialTxn> listaFinancialTxn = dao.findAllByAssetClass(code);
        if (listaFinancialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinancialTxn, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/financial_txn")
    public ResponseEntity<FinancialTxn> saveOrUpdate(@RequestBody FinancialTxn financialTxn) {
        try {
            if (financialTxn.getIdFinancialTxn() == 0) {
                financialTxn.setIdFinancialTxn(null);
                financialTxn.setTxnStatus(txnStatusDAO.findByCode("PENDING"));
            }

            financialTxn = dao.saveOrUpdate(financialTxn);
            
            return new ResponseEntity(financialTxn, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
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
    public ResponseEntity<FinancialTxn> logicalDelete(@PathVariable("id") Integer idFinancialTxn) {
        FinancialTxn financialTxn = dao.logicalDelete(idFinancialTxn);
        if (financialTxn != null) {
            return new ResponseEntity(financialTxn, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
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
