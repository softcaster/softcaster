package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.CashFlowItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashFlowItemRestController {

    @Autowired
    private CashFlowItemDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/cash_flow_item/r0")
    public ResponseEntity findAll() {
        List<CashFlowItem> listaCashFlowItem = dao.findAll();
        if (listaCashFlowItem == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCashFlowItem, HttpStatus.OK);
    }

    @GetMapping("/cash_flow_item/r1/{id}")
    public ResponseEntity findByIdCashFlowItem(@PathVariable("id") Integer idCashFlowItem) {
        CashFlowItem cashFlowItem = dao.findByIdCashFlowItem(idCashFlowItem);
        if (cashFlowItem == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(cashFlowItem, HttpStatus.OK);
    }

}
