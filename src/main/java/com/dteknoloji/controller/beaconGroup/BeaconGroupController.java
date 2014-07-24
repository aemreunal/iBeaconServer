package com.dteknoloji.controller.beaconGroup;

/*
 * This code belongs to:
 * Ahmet Emre Unal
 * S001974
 * emre.unal@ozu.edu.tr
 */

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.domain.beaconGroup.BeaconGroup;
import com.dteknoloji.service.beaconGroup.BeaconGroupService;

@Controller
@RequestMapping("/BeaconGroup")
public class BeaconGroupController {
    @Autowired
    private BeaconGroupService service;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<BeaconGroup>> getAllBeaconGroups() {
        return new ResponseEntity<List<BeaconGroup>>(service.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<BeaconGroup> viewBeaconGroup(@PathVariable String id) {
        BeaconGroup beaconGroup = service.findById(Long.valueOf(id));
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BeaconGroup> createBeaconGroup(@RequestBody BeaconGroup restBeaconGroup, UriComponentsBuilder builder) {
        try {
            BeaconGroup newBeaconGroup = service.save(restBeaconGroup);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved beacon group with ID = \'" + newBeaconGroup.getBeaconGroupId() + "\' name = \'" + newBeaconGroup.getName() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/BeaconGroup/{id}").buildAndExpand(newBeaconGroup.getBeaconGroupId().toString()).toUri());
            return new ResponseEntity<BeaconGroup>(newBeaconGroup, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon group! Constraint violation detected!");
            }
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(method = RequestMethod.POST,value = "/{id}/beacon")
    public ResponseEntity<BeaconGroup> createBeaconWithGroup(@RequestBody BeaconGroup restBeaconGroup,@PathVariable String id, UriComponentsBuilder builder) {
        try {
            BeaconGroup newBeaconGroup = service.save(restBeaconGroup);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved beacon group with ID = \'" + newBeaconGroup.getBeaconGroupId() + "\' name = \'" + newBeaconGroup.getName() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/BeaconGroup/{id}").buildAndExpand(newBeaconGroup.getBeaconGroupId().toString()).toUri());
            return new ResponseEntity<BeaconGroup>(newBeaconGroup, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon group! Constraint violation detected!");
            }
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<BeaconGroup> deleteBeaconGroup(@PathVariable String id) {

        BeaconGroup beaconGroup = service.findById(Long.valueOf(id));
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }

        boolean deleted = service.delete(Long.valueOf(id));
        if (deleted) {
            return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
        }

        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.FORBIDDEN);
    }
}
