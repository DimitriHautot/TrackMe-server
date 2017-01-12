package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import net.trackme.server.domain.Trip;

/**
 * @author Dimitri (10/01/2017)
 */
public class Persistence extends AbstractVerticle {

    public static final String SAVE = "persistence.trip.save";
    public static final String READ   = "persistence.trip.read";
    public static final String DELETE = "persistence.trip.delete";

    private MongoClient mongo;

    @Override
    public void start() {
        // FIXME Externalize this
        JsonObject configuration = new JsonObject()
                .put("host", "localhost")
                .put("port", 27017)
                .put("db_name", "track-me");
        mongo = MongoClient.createShared(vertx, configuration);

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(SAVE, this::save);
        eventBus.consumer(READ, this::read);
        eventBus.consumer(DELETE, this::delete);
    }

    @Override
    public void stop() {
        mongo.close();
    }

    private void save(Message<JsonObject> message) {
        boolean insert = message.body().getString("_id") == null;
        mongo.save("trips", message.body(), asyncResult -> {
            if (asyncResult.succeeded()) {
                if (insert) {
                    message.reply(message.body().put("_id", asyncResult.result()));
                } else {
                    message.reply(asyncResult.result());
                }
            }
        });
    }

    private void delete(Message<Trip> message) {
    }

    private void read(Message<String> message) {
        JsonObject query = new JsonObject().put("_id", message.body());

        mongo.findOne("trips", query, null, asyncResult -> {
            if (asyncResult.succeeded()) {
                message.reply(asyncResult.result());
            }
        });
    }
}
