package com.dteknoloji.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.domain.Beacon;
import com.dteknoloji.service.BeaconService;

@Controller
@RequestMapping("/beacon")
public class BeaconController {

    @Autowired
    private BeaconService service;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Beacon>> getAllBeacons(@RequestParam(value="uuid", required=false, defaultValue = "") String uuid) {
        if (uuid.equals("")) {
            return new ResponseEntity<List<Beacon>>(service.findAll(), HttpStatus.OK);
        } else {
            return getBeaconsWithMatchingUuid(uuid);
        }
    }

    private ResponseEntity<List<Beacon>> getBeaconsWithMatchingUuid(String uuid) {
        List<Beacon> beacons = service.findBeaconByUuid(uuid);
        if (beacons.size() == 0) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Beacon>>(beacons, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Beacon> viewBeacon(@PathVariable String id) {
        Beacon beacon = service.findById(Long.valueOf(id));
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Beacon> createBeacon(@RequestBody Beacon restBeacon, UriComponentsBuilder builder) {

        Beacon newBeacon = service.save(restBeacon);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/beacons/{id}").buildAndExpand(newBeacon.getBeaconId().toString()).toUri());

        return new ResponseEntity<Beacon>(newBeacon, headers, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    @ResponseBody
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
