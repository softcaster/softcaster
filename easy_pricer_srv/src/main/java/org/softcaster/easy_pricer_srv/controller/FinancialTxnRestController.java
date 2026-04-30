package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.FinancialTxnDAO;
import org.softcaster.easy_pricer_core.data.TxnStatusDAO;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity findAll() {
        List<FinancialTxn> listaFinancialTxn = dao.findAll();
        if (listaFinancialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinancialTxn, HttpStatus.OK);
    }

    @GetMapping("/financial_txn/r02/{id}")
    public ResponseEntity findByIdFinancialTxn(@PathVariable("id") Integer idFinancialTxn) {
        FinancialTxn financialTxn = dao.findByIdFinancialTxn(idFinancialTxn);
        if (financialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(financialTxn, HttpStatus.OK);
    }
    
    @GetMapping("/financial_txn/r03/{code}")
    public ResponseEntity findAllByAssetClass(@PathVariable("code") String code) {
        List<FinancialTxn> listaFinancialTxn = dao.findAllByAssetClass(code);
        if (listaFinancialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinancialTxn, HttpStatus.OK);
    }
    
    // save/update record
    @PostMapping(value = "/financial_txn")
    public ResponseEntity saveOrUpdate(@RequestBody FinancialTxn financialTxn) {
        try {
            if (financialTxn.getIdFinancialTxn()== 0) {
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
    public ResponseEntity delete(@PathVariable("id") Integer idFinancialTxn) {
        FinancialTxn financialTxn = dao.findByIdFinancialTxn(idFinancialTxn);
        if (financialTxn != null) {
            dao.delete(financialTxn);
            return new ResponseEntity(financialTxn, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
