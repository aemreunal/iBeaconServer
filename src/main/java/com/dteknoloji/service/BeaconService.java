package com.dteknoloji.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dteknoloji.domain.Beacon;
import com.dteknoloji.repository.BeaconRepository;

@Transactional
@Service
public class BeaconService {
    @Autowired
    BeaconRepository repository;

    public Beacon save(Beacon beacon) {
        return repository.save(beacon);
    }

    public List<Beacon> findAll() {
        List<Beacon> beaconList = new ArrayList<Beacon>();

        for (Beacon beacon : repository.findAll()) {
            beaconList.add(beacon);
        }

        return beaconList;
    }

    public Beacon findById(Long id) {

        return repository.findOne(id);
    }

    public List<Beacon> findBeaconByUuid(String uuid) {
        return repository.findBeaconsByUuid(uuid);
    }

    public boolean delete(Long id) {
        repository.delete(id);

        return true;
    }

}
