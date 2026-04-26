package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.FinacialTxn;
import org.softcaster.easy_pricer_core.data.FinacialTxnDAO;
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
public class FinacialTxnRestController {

    @Autowired
    private FinacialTxnDAO dao;
    @Autowired
    private TxnStatusDAO txnStatusDAO;

    @GetMapping("/finacial_txn/r01")
    public ResponseEntity findAll() {
        List<FinacialTxn> listaFinacialTxn = dao.findAll();
        if (listaFinacialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFinacialTxn, HttpStatus.OK);
    }

    @GetMapping("/finacial_txn/r02/{id}")
    public ResponseEntity findByIdFinacialTxn(@PathVariable("id") Integer idFinacialTxn) {
        FinacialTxn finacialTxn = dao.findByIdFinacialTxn(idFinacialTxn);
        if (finacialTxn == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(finacialTxn, HttpStatus.OK);
    }
    
    // save/update record
    @PostMapping(value = "/finacial_txn")
    public ResponseEntity saveOrUpdate(@RequestBody FinacialTxn finacialTxn) {
        try {
            if (finacialTxn.getIdFinacialTxn()== 0) {
                finacialTxn.setIdFinacialTxn(null);
                finacialTxn.setTxnStatus(txnStatusDAO.findByCode("PENDING"));
            }

            finacialTxn = dao.saveOrUpdate(finacialTxn);
            return new ResponseEntity(finacialTxn, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    // delete record
    @DeleteMapping("/finacial_txn/d01/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idFinacialTxn) {
        FinacialTxn finacialTxn = dao.findByIdFinacialTxn(idFinacialTxn);
        if (finacialTxn != null) {
            dao.delete(finacialTxn);
            return new ResponseEntity(finacialTxn, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
