package com.dteknoloji.repository.beacon;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.dteknoloji.domain.beacon.Beacon;

// extends CrudRepository<Object type, Object ID type>
public interface BeaconRepository extends CrudRepository<Beacon, Long>, JpaSpecificationExecutor {
//    List<Beacon> findBeaconsByUuid(String uuid);
//    List<Beacon> findBeaconsByGroupId(Long groupId);

}
