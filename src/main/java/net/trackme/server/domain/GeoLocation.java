package net.trackme.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Dimitri (12/01/2017)
 */
@Getter
@Setter
@Builder
public class GeoLocation {

    private long timestamp;
    private double latitude, longitude;
    private String comment;

    @JsonCreator
    public GeoLocation(@JsonProperty("timestamp") long timestamp,
                       @JsonProperty("latitude") double latitude,
                       @JsonProperty("longitude") double longitude,
                       @JsonProperty("comment") String comment)
    {
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
    }

    public GeoLocation(Map<String, Object> map) {
        this.timestamp = (int) map.get("timestamp");
        this.latitude = (double) map.get("latitude");
        this.longitude = (double) map.get("longitude");
        this.comment = (String) map.get("comment");
    }
}
