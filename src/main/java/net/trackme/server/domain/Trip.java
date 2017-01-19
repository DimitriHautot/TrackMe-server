package net.trackme.server.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Dimitri (10/01/2017)
 */
@Getter
@Builder
public class Trip {

    private String _id;
    private String ownershipToken;
    private String description; // e.g. Trip to Bordeaux
    private Status status; // Paused
    private String statusRemark; // Pee
    private List<GeoLocation> geoLocations;

    public Trip(String _id, String ownershipToken, String description, Status status, String statusRemark, List<GeoLocation> geoLocations) {
        this._id = _id;
        this.ownershipToken = ownershipToken;
        this.description = description;
        this.status = status;
        this.statusRemark = statusRemark;
        this.geoLocations = geoLocations;
    }
}
