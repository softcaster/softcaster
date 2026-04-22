package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.LoanMasterData;
import org.softcaster.easy_pricer_core.data.LoanMasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanMasterDataRestController {

    @Autowired
    private LoanMasterDataDAO dao;

    @GetMapping("/loan_master_data/r0")
    public ResponseEntity findAll() {
        List<LoanMasterData> listaLoanMasterData = dao.findAll();
        if (listaLoanMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaLoanMasterData, HttpStatus.OK);
    }

    @GetMapping("/loan_master_data/r1/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        LoanMasterData loanMasterData = dao.findByIdMasterData(idMasterData);
        if (loanMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(loanMasterData, HttpStatus.OK);
    }
}
