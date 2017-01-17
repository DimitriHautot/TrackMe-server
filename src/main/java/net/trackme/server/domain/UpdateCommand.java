package net.trackme.server.domain;

import io.vertx.core.json.JsonArray;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Dimitri (17/01/2017)
 */
@Getter
@Builder
public class UpdateCommand {

    private String ownershipToken;
    private String tripId;
    private JsonArray items;
}
