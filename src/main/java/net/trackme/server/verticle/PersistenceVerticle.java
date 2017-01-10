package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import net.trackme.server.verticle.domain.Trip;

/**
 * @author Dimitri (10/01/2017)
 */
public class PersistenceVerticle extends AbstractVerticle {

    public static final String CREATE = "persistence.trip.create";
    public static final String READ   = "persistence.trip.read";
    public static final String UPDATE = "persistence.trip.update";
    public static final String DELETE = "persistence.trip.delete";

    private MongoClient mongo;

    public PersistenceVerticle() {
        super();
        JsonObject configuration = new JsonObject(); // FIXME Provide real values
        mongo = MongoClient.createShared(vertx, configuration);
    }

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();

        eventBus.consumer("persistence.trip.create", this::create);
        eventBus.consumer("persistence.trip.update", this::update);
        eventBus.consumer("persistence.trip.delete", this::delete);
        eventBus.consumer("persistence.trip.read", this::read);
    }

    private void create(Message<Trip> message) {
    }

    private void update(Message<Trip> message) {
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
