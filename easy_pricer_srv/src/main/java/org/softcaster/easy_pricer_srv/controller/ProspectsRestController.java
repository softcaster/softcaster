/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.dto.PositionProspectDto;
import org.softcaster.core.dto.ProspectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProspectsRestController {

    @Autowired
    PositionDetailDAO positionDetailDAO;

    @PostMapping(value = "/prospects/position")
    public ResponseEntity<List<PositionProspectDto>> getPositionProspect(@RequestBody ProspectFilter filter) {
        List<PositionProspectDto> ppList = positionDetailDAO.getPositionProspect(filter.getPositionId(), filter.getCounterpartyId());
        if (ppList == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(ppList, HttpStatus.OK);
        }
    }
}
