package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.trackme.server.domain.Trip;
import net.trackme.server.domain.UpdateCommand;

/**
 * Verticle responsible to hold business logic related to {@link net.trackme.server.domain.Trip} instances.
 *
 * @author Dimitri (17/01/2017)
 */
public class TripVerticle extends AbstractVerticle {

    public static final String UPDATE = "business.trip.update";

    @Override
    public void start() throws Exception {
        super.start();

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(UPDATE, this::update);
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
        // TODO Apply each item on the trip here
    }

    private void persist(Trip trip, Message message) {
        JsonObject jsonTrip = new JsonObject(Json.encode(trip));
        vertx.eventBus().send(Persistence.UPDATE, jsonTrip, asyncResult -> {
            message.reply(204);
        });
    }
}
