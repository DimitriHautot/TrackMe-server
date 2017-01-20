package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.trackme.server.domain.GeoLocation;
import net.trackme.server.domain.Status;
import net.trackme.server.domain.Trip;
import net.trackme.server.domain.UpdateCommand;

import java.util.ArrayList;
import java.util.Map;

/**
 * Verticle responsible to hold business logic related to {@link net.trackme.server.domain.Trip} instances.
 *
 * @author Dimitri (17/01/2017)
 */
public class TripVerticle extends AbstractVerticle {

    public static final String UPDATE = "business.trip.update";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(UPDATE, this::update);

        startFuture.complete();
    }

    /**
     * <ol>
     *     <li>make sure the ownershipToken is valid for the trip</li>
     *     <li>apply the changes</li>
     *     <li>persist the updated trip</li>
     *     <li>return success to caller</li>
     * </ol>
     */
    private void update(Message<String> message) {
        UpdateCommand command = Json.decodeValue(message.body(), UpdateCommand.class);
        vertx.eventBus().send(Persistence.READ, command.getTripId(), asyncResult -> {
            if (asyncResult.succeeded()) {
                Trip trip = Json.decodeValue(asyncResult.result().body().toString(), Trip.class);
                if (!trip.getOwnershipToken().equals(command.getOwnershipToken())) {
                    message.reply(403);
                }
                apply(trip, command);
                persist(trip, message);
            }
        });
    }

    private void apply(Trip trip, UpdateCommand updateCommand) {
        updateCommand.getItems().forEach(item -> {
            switch (item.getName()) {
                case "description":
                    trip.setDescription(item.getValue() != null ? item.getValue().toString() : null);
                    break;
                case "status":
                    for (Status status : Status.values()) {
                        if (status.name().equals(item.getValue())) {
                            trip.setStatus(status);
                            break;
                        }
                    }
                    break;
                case "statusRemark":
                    trip.setStatusRemark(item.getValue() != null ? item.getValue().toString() : null);
                    break;
                case "geoLocation":
                    if (item.getValue() != null) {
                        if (trip.getGeoLocations() == null) {
                            trip.setGeoLocations(new ArrayList<>());
                        }
                        trip.getGeoLocations().add(new GeoLocation((Map<String, Object>) item.getValue()));
                    }
                    break;
            }
        });
    }

    private void persist(Trip trip, Message message) {
        JsonObject jsonTrip = new JsonObject(Json.encode(trip));
        vertx.eventBus().send(Persistence.UPDATE, jsonTrip, asyncResult -> {
            message.reply(204);
        });
    }
}
