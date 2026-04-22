package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.AssetClass;
import org.softcaster.easy_pricer_core.data.AssetClassDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssetClassRestController {

    @Autowired
    private AssetClassDAO dao;

    @Autowired

    @GetMapping("/asset_class/r0")
    public ResponseEntity findAll() {
        List<AssetClass> listaAssetClass = dao.findAll();
        if (listaAssetClass == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaAssetClass, HttpStatus.OK);
    }

    @GetMapping("/asset_class/r1/{id}")
    public ResponseEntity findByIdAssetClass(@PathVariable("id") Integer idAssetClass) {
        AssetClass assetClass = dao.findByIdAssetClass(idAssetClass);
        if (assetClass == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(assetClass, HttpStatus.OK);
    }

}
