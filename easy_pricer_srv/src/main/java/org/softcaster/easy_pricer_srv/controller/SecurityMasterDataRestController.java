package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.SecurityMasterData;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityMasterDataRestController {

    @Autowired
    private SecurityMasterDataDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/security_master_data/r01")
    public ResponseEntity findAll() {
        List<SecurityMasterData> listaSecurityMasterData = dao.findAll();
        if (listaSecurityMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaSecurityMasterData, HttpStatus.OK);
    }

    @GetMapping("/security_master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        SecurityMasterData securityMasterData = dao.findByIdMasterData(idMasterData);
        if (securityMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(securityMasterData, HttpStatus.OK);
    }

    @GetMapping("/security_master_data/r03/{id}")
    public ResponseEntity findByIsin(@PathVariable("id") String isin) {
        SecurityMasterData securityMasterData = dao.findByIsin(isin);
        if (securityMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(securityMasterData, HttpStatus.OK);
    }

    @GetMapping("/security_master_data/r04/{id}")
    public ResponseEntity findByCurrency(@PathVariable("id") String currencyCode) {
        List<SecurityMasterData> securityMasterDataList = dao.findByCurrency(currencyCode);
        if (securityMasterDataList == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(securityMasterDataList, HttpStatus.OK);
    }

    @GetMapping("/security_master_data/r05/{id}")
    public ResponseEntity findByDescriptionContaining(@PathVariable("id") String issueDescriptionFragment) {
        List<SecurityMasterData> securityMasterDataList = dao.findByDescriptionContaining(issueDescriptionFragment);
        if (securityMasterDataList == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(securityMasterDataList, HttpStatus.OK);
    }

    @GetMapping("/security_master_data/r06/{id}")
    public ResponseEntity findAllByAssetClass(@PathVariable("id") String code) {
        List<SecurityMasterData> listaSecurityMasterData = dao.findAllByAssetClass(code);
        if (listaSecurityMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaSecurityMasterData, HttpStatus.OK);
    }
    
    // save/update record
    @PostMapping(value = "/security_master_data")
    public ResponseEntity saveOrUpdate(@RequestBody SecurityMasterData securityMasterData) {
        try {
            if (securityMasterData.getIdMasterData() == 0) {
                securityMasterData.setIdMasterData(null);
            }

            securityMasterData = dao.saveOrUpdate(securityMasterData);
            return new ResponseEntity(securityMasterData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/security_master_data/d01/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idMasterData) {
        SecurityMasterData securityMasterData = dao.findByIdMasterData(idMasterData);
        if (securityMasterData != null) {
            dao.delete(securityMasterData);
            return new ResponseEntity(securityMasterData, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
