package net.trackme.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Dimitri (10/01/2017)
 */
@Getter
@Setter
@Builder
public class Trip {

    @Setter(AccessLevel.NONE)
    private String _id;
    @Setter(AccessLevel.NONE)
    private String ownershipToken;

    private String description; // e.g. Trip to Bordeaux
    private Status status; // Paused
    private String statusRemark; // Pee
    private List<GeoLocation> geoLocations;

    @JsonCreator
    public Trip(@JsonProperty(value = "_id") String _id,
                @JsonProperty(value = "ownershipToken") String ownershipToken,
                @JsonProperty(value = "description") String description,
                @JsonProperty(value = "status") Status status,
                @JsonProperty(value = "statusRemark") String statusRemark,
                @JsonProperty(value = "geoLocations") List<GeoLocation> geoLocations)
    {
        this._id = _id;
        this.ownershipToken = ownershipToken;
        this.description = description;
        this.status = status;
        this.statusRemark = statusRemark;
        this.geoLocations = geoLocations;
    }
}
