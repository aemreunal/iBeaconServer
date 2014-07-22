package com.dteknoloji.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.dteknoloji.domain.Beacon;

// extends CrudRepository<Object type, Object ID type>
public interface BeaconRepository extends CrudRepository<Beacon, Long> {
    //    @Query("SELECT * FROM BEACONS WHERE UUID = ")
    List<Beacon> findBeaconsByUuid(String uuid);

    //  @Query("SELECT a FROM Customer c INNER JOIN c.applicationList a WHERE c.identity = :customerId AND a.identity= :appId")
    //  Application findApplicationById(@Param("customerId") Long customerId, @Param("appId") Long appId);
}
