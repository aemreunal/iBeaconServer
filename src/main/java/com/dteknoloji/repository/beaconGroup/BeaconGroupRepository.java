package com.dteknoloji.repository.beaconGroup;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.dteknoloji.domain.beaconGroup.BeaconGroup;

// extends CrudRepository<Object type, Object ID type>
public interface BeaconGroupRepository extends CrudRepository<BeaconGroup, Long>, JpaSpecificationExecutor {

}
