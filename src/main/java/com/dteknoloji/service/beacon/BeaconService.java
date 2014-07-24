package com.dteknoloji.service.beacon;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.domain.beacon.Beacon;
import com.dteknoloji.repository.beacon.BeaconRepository;
import com.dteknoloji.repository.beacon.BeaconSpecifications;

@Transactional
@Service
public class BeaconService {
    @Autowired
    private BeaconRepository repository;

    public Beacon save(Beacon beacon) {
        // TODO Create group if beaconGroup doesn't exist
        return repository.save(beacon);
    }

    public List<Beacon> findAll() {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding all beacons");
        }

        List<Beacon> beaconList = new ArrayList<Beacon>();

        for (Beacon beacon : repository.findAll()) {
            beaconList.add(beacon);
        }

        return beaconList;
    }

    public List<Beacon> findBeaconsBySpecs(String uuid, String major, String minor) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacons with UUID = \'" + uuid + "\' major = \'" + major + "\' minor = \'" + minor + "\'");
        }

        return repository.findAll(BeaconSpecifications.beaconWithSpecification(uuid, major, minor));
    }

    public Beacon findById(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon with ID = \'" + id + "\'");
        }

        return repository.findOne(id);
    }

    public boolean delete(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon with ID = \'" + id + "\'");
        }

        if (id != null) {
            repository.delete(id);
            return true;
        } else {
            return false;
        }
    }

}
