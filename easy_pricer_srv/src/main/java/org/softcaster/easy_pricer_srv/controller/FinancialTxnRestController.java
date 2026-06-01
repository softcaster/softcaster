package org.softcaster.easy_pricer_srv.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.TxnStatusDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.core.dto.FinancialTxnMapper;
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
    @Autowired
    FinancialTxnMapper mapper;

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

    protected boolean updateOnly(FinancialTxn oldTxn, FinancialTxn newTxn) {

        if (oldTxn != null && newTxn != null) {
            // Comparo prezzi
            if (Double.compare(newTxn.getPrice(), oldTxn.getPrice()) != 0) {
                return false;
            }
            // Comparo quantita
            if (Double.compare(newTxn.getQuantity(), oldTxn.getQuantity()) != 0) {
                return false;
            }
            // Comparo controparte
            if (!newTxn.getCounterparty().getCode().equals(oldTxn.getCounterparty().getCode())) {
                return false;
            }
        }

        return true;
    }

    // save/update record
    @PostMapping(value = "/financial_txn")
    public ResponseEntity<FinancialTxnDto> saveOrUpdate(@RequestBody FinancialTxnDto financialTxnDto) {
        try {
            FinancialTxn financialTxn = mapper.fromDto(financialTxnDto);
            if (financialTxn != null) {
                // caso nuovo inserimento
                if (financialTxn.getIdFinancialTxn() == 0) {
                    financialTxn.setIdFinancialTxn(null);
                    financialTxn.setTxnStatus(txnStatusDAO.findByCode("PENDING"));
                    financialTxn.setValueDate(financialTxn.getTradeDate());
                } // caso modifica
                else {
                    // Recupero txn 
                    FinancialTxn oldTxn = dao.findByIdFinancialTxn(financialTxn.getIdFinancialTxn());
                    if (oldTxn.getTxnStatus().getCode().equals("EXECUTED")) {
                        // Se modifica contabile allora marco la vecchia come CANCELLED
                        // ed inserisco una nuova transazione a pending
                        if (!updateOnly(oldTxn, financialTxn)) {
                            // Marco la vecchia txn come cancellata
                            oldTxn.setTxnStatus(txnStatusDAO.findByCode("CANCELLED"));
                            dao.saveOrUpdate(oldTxn);
                            // Creo una nuova txn
                            financialTxn.setTxnStatus(txnStatusDAO.findByCode("PENDING"));
                            financialTxn.setIdFinancialTxn(null);
                            financialTxn.setRefId(oldTxn.getIdFinancialTxn());
                        }
                    }
                }

                financialTxn = dao.saveOrUpdate(financialTxn);

                return new ResponseEntity(financialTxnDto, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("Null Transaction"), HttpStatus.NOT_ACCEPTABLE);
            }
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
