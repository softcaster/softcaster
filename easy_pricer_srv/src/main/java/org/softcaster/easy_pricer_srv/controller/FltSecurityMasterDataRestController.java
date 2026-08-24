package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.FltSecurityMasterDataDAO;
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
public class FltSecurityMasterDataRestController {
	@Autowired
	private FltSecurityMasterDataDAO dao;

	@Autowired
	private ApplicationContext appContext;

	@GetMapping("/flt_security_master_data/r0")
	public ResponseEntity findAll() {
		List<FltSecurityMasterData> listaFltSecurityMasterData=dao.findAll();
		if(listaFltSecurityMasterData== null) {
			return new ResponseEntity(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(listaFltSecurityMasterData, HttpStatus.OK);
	}

	@GetMapping("/flt_security_master_data/r1/{id}")
	public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
		FltSecurityMasterData fltSecurityMasterData = dao.findByIdMasterData(idMasterData);
		if(fltSecurityMasterData== null) {
			return new ResponseEntity(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(fltSecurityMasterData, HttpStatus.OK);
	}

    // save/update record
    @PostMapping(value = "/flt_security_master_data")
    public ResponseEntity saveOrUpdate(@RequestBody FltSecurityMasterData fltSecurityMasterData) {
        try {
            if (fltSecurityMasterData.getIdMasterData() == 0) {
                fltSecurityMasterData.setIdMasterData(null);
            }

            fltSecurityMasterData = dao.saveOrUpdate(fltSecurityMasterData);
            return new ResponseEntity(fltSecurityMasterData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/flt_security_master_data/d01/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idMasterData) {
        FltSecurityMasterData fltSecurityMasterData = dao.findByIdMasterData(idMasterData);
        if (fltSecurityMasterData != null) {
            dao.delete(fltSecurityMasterData);
            return new ResponseEntity(fltSecurityMasterData, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }}
