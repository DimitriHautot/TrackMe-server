package net.trackme.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Dimitri (17/01/2017)
 */
@Getter
@Builder
public class UpdateCommand {

    private String ownershipToken;
    private String tripId;
    private List<JsonObject> items;

    @JsonCreator
    public UpdateCommand(@JsonProperty(value = "ownershipToken") String ownershipToken,
                         @JsonProperty(value = "tripId") String tripId,
                         @JsonProperty(value = "items") List<JsonObject> items)
    {
        this.ownershipToken = ownershipToken;
        this.tripId = tripId;
        this.items = items;
    }

//    @Getter @Setter
//    private class UpdateCommandItem {
//        private String name, value;
//    }
}
