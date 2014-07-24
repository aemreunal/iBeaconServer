package com.dteknoloji.controller.beacon;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.domain.beacon.Beacon;
import com.dteknoloji.service.beacon.BeaconService;

@Controller
@RequestMapping("/Beacon")
public class BeaconController {
    @Autowired
    private BeaconService service;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Beacon>> getAllBeacons(
            @RequestParam(value="uuid", required=false, defaultValue = "") String uuid,
            @RequestParam(value="major", required=false, defaultValue = "") String major,
            @RequestParam(value="minor", required=false, defaultValue = "") String minor) {
        if (uuid.equals("") && major.equals("") && minor.equals("")) {
            return new ResponseEntity<List<Beacon>>(service.findAll(), HttpStatus.OK);
        } else {
            return getBeaconsWithMatchingCriteria(uuid, major, minor);
        }
    }

    private ResponseEntity<List<Beacon>> getBeaconsWithMatchingCriteria(String uuid, String major, String minor) {
        List<Beacon> beacons = service.findBeaconsBySpecs(uuid, major, minor);

        if (beacons.size() == 0) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Beacon>>(beacons, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> viewBeacon(@PathVariable String id) {
        Beacon beacon = service.findById(Long.valueOf(id));
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Beacon> createBeacon(@RequestBody Beacon restBeacon, UriComponentsBuilder builder) {
        try {
            Beacon newBeacon = service.save(restBeacon);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() + "\' major = \'" + newBeacon.getMajor() + "\' minor = \'" + newBeacon.getMinor() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/Beacon/{id}").buildAndExpand(newBeacon.getBeaconId().toString()).toUri());
            return new ResponseEntity<Beacon>(newBeacon, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon! Constraint violation detected!");
            }
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String id) {

        Beacon beacon = service.findById(Long.valueOf(id));
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }

        boolean deleted = service.delete(Long.valueOf(id));
        if (deleted) {
            return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
        }

        return new ResponseEntity<Beacon>(beacon, HttpStatus.FORBIDDEN);
    }

}
