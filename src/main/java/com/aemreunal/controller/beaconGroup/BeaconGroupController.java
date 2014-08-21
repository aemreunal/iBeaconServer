package com.aemreunal.controller.beaconGroup;

/*
 **************************
 * Copyright (c) 2014     *
 *                        *
 * This code belongs to:  *
 *                        *
 * Ahmet Emre Ünal        *
 * S001974                *
 *                        *
 * aemreunal@gmail.com    *
 * emre.unal@ozu.edu.tr   *
 *                        *
 * aemreunal.com          *
 **************************
 */

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.service.BeaconGroupService;

@Controller
@RequestMapping(GlobalSettings.BEACONGROUP_PATH_MAPPING)
public class BeaconGroupController {
    @Autowired
    private BeaconGroupService beaconGroupService;

    /**
     * Get beacon groups that belong to a project.
     *
     * @param projectId
     *     The ID of the project
     *
     * @return The list of beacon groups that belong to the project with the specified ID
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<BeaconGroup>> viewBeaconGroupsOfProject(@PathVariable String username,
                                                                       @PathVariable Long projectId,
                                                                       @RequestParam(value = "name", required = false, defaultValue = "") String beaconGroupName) {
        if (beaconGroupName.equals("")) {
            List<BeaconGroup> beaconGroups = beaconGroupService.getAllBeaconGroupsOf(username, projectId);
            return new ResponseEntity<List<BeaconGroup>>(beaconGroups, HttpStatus.OK);
        } else {
            List<BeaconGroup> beaconGroups = beaconGroupService.findBeaconGroupsBySpecs(username, projectId, beaconGroupName);
            return new ResponseEntity<List<BeaconGroup>>(beaconGroups, HttpStatus.OK);
        }
    }

    /**
     * Get the beacon group with specified ID
     *
     * @param projectId
     *     The ID of the project
     * @param beaconGroupId
     *     The ID of the group
     *
     * @return The beacon group
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.BEACONGROUP_ID_MAPPING, produces = "application/json")
    public ResponseEntity<BeaconGroup> viewBeaconGroup(@PathVariable String username,
                                                       @PathVariable Long projectId,
                                                       @PathVariable Long beaconGroupId) {
        BeaconGroup beaconGroup = beaconGroupService.getBeaconGroup(username, projectId, beaconGroupId);
        // TODO add links
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }

    /**
     * Get beacons that belong to to the specified beacon group
     *
     * @param projectId
     *     The ID of the project to operate in
     * @param beaconGroupId
     *     The ID of the group
     *
     * @return The list of beacons that belong to the group
     */
    // TODO maybe return just the list of Beacon IDs, queried from beacon_groups_to_beacon
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.BEACONGROUP_MEMBERS_MAPPING, produces = "application/json")
    public ResponseEntity<List<Beacon>> viewBeaconGroupMembers(@PathVariable String username,
                                                               @PathVariable Long projectId,
                                                               @PathVariable Long beaconGroupId) {
        List<Beacon> beaconList = beaconGroupService.getMembersOfBeaconGroup(username, projectId, beaconGroupId);
        return new ResponseEntity<List<Beacon>>(beaconList, HttpStatus.OK);
    }

    /**
     * Create a new beacon group in project
     * <p/>
     *
     * @param projectId
     *     The ID of the project to create the beacon group in
     * @param beaconGroupFromJson
     *     The beacon group as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created beacon group
     */
    // TODO {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<BeaconGroup> createBeaconGroupInProject(@PathVariable String username,
                                                                  @PathVariable Long projectId,
                                                                  @RequestBody BeaconGroup beaconGroupFromJson,
                                                                  UriComponentsBuilder builder) {
        BeaconGroup savedBeaconGroup = beaconGroupService.save(username, projectId, beaconGroupFromJson);
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved beacon group with ID = \'" + savedBeaconGroup.getBeaconGroupId() +
                                   "\' name = \'" + savedBeaconGroup.getName() +
                                   "\' in project with ID = \'" + projectId + "\'");
        }

        return buildCreateResponse(username, builder, savedBeaconGroup);
    }

    private ResponseEntity<BeaconGroup> buildCreateResponse(String username, UriComponentsBuilder builder, BeaconGroup savedBeaconGroup) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.BEACONGROUP_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       username,
                                       savedBeaconGroup.getProject().getProjectId(),
                                       savedBeaconGroup.getBeaconGroupId())
                                   .toUri());
        return new ResponseEntity<BeaconGroup>(savedBeaconGroup, headers, HttpStatus.CREATED);
    }

    /**
     * Add beacon to the specified beacon group.
     * <p/>
     * Can return 409 if beacon already has a group.
     * <p/>
     * Ex: "/BeaconGroup/1/Add?beaconId=12"
     *
     * @param projectId
     *     The ID of the project to operate in
     * @param beaconGroupId
     *     The ID of the beacon group to add the beacon to
     * @param beaconId
     *     The ID of the beacon to add
     *
     * @return The added beacon group
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.BEACONGROUP_ADD_MEMBER_MAPPING, produces = "application/json")
    public ResponseEntity<BeaconGroup> addBeaconToGroup(@PathVariable String username,
                                                        @PathVariable Long projectId,
                                                        @PathVariable Long beaconGroupId,
                                                        @RequestParam(value = "beaconId", required = true) Long beaconId) {
        BeaconGroup beaconGroup = beaconGroupService.addBeaconToGroup(username, projectId, beaconGroupId, beaconId);
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }

    /**
     * Remove beacon from the specified beacon group.
     * <p/>
     * Can return 400 if beacon does not have a group.
     * <p/>
     * Ex: "/BeaconGroup/1/Remove?beaconId=12"
     *
     * @param projectId
     *     The ID of the project to operate in
     * @param beaconGroupId
     *     The ID of the beacon group to remove the beacon from
     * @param beaconId
     *     The ID of the beacon to remove
     *
     * @return The removed beacon group
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.BEACONGROUP_REMOVE_MEMBER_MAPPING, produces = "application/json")
    public ResponseEntity<BeaconGroup> removeBeaconFromGroup(@PathVariable String username,
                                                             @PathVariable Long projectId,
                                                             @PathVariable Long beaconGroupId,
                                                             @RequestParam(value = "beaconId", required = true) Long beaconId) {
        BeaconGroup beaconGroup = beaconGroupService.removeBeaconFromGroup(username, projectId, beaconGroupId, beaconId);
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }


    /**
     * Delete the specified beacon group
     *
     * @param projectId
     *     The ID of the project to operate in
     * @param beaconGroupId
     *     The ID of the beacon group to delete
     *
     * @return The deleted beacon group
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.BEACONGROUP_ID_MAPPING, produces = "application/json")
    public ResponseEntity<BeaconGroup> deleteBeaconGroup(@PathVariable String username,
                                                         @PathVariable Long projectId,
                                                         @PathVariable Long beaconGroupId,
                                                         @RequestParam(value = "confirm", required = true) String confirmation) {

        if (confirmation.toLowerCase().equals("yes")) {
            BeaconGroup deletedBeaconGroup = beaconGroupService.delete(username, projectId, beaconGroupId);
            return new ResponseEntity<BeaconGroup>(deletedBeaconGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity<BeaconGroup>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
