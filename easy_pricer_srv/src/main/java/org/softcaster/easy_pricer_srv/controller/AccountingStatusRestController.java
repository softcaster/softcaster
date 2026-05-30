package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.AccountingStatus;
import org.softcaster.core.data.AccountingStatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountingStatusRestController {

    @Autowired
    private AccountingStatusDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/accounting_status/r02/{id}")
    public ResponseEntity findByAccountingStatusId(@PathVariable("id") Integer accountingStatusId) {
        AccountingStatus accountingStatus = dao.findByAccountingStatusId(accountingStatusId);
        if (accountingStatus == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(accountingStatus, HttpStatus.OK);
    }

}
