package net.trackme.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private List<UpdateCommandItem> items;

    @JsonCreator
    public UpdateCommand(@JsonProperty(value = "ownershipToken") String ownershipToken,
                         @JsonProperty(value = "tripId") String tripId,
                         @JsonProperty(value = "items") List<UpdateCommandItem> items)
    {
        this.ownershipToken = ownershipToken;
        this.tripId = tripId;
        this.items = items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommandItem {
        private String name;
        private Object value;
    }
}
