package net.trackme.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    public GeoLocation(JsonObject jsonObject) {
        this.timestamp = jsonObject.getLong("timestamp");
        this.latitude = jsonObject.getDouble("latitude");
        this.longitude = jsonObject.getDouble("longitude");
        this.comment = jsonObject.getString("comment");
    }
}
