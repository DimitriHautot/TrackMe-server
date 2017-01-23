package net.trackme.server.service.persistence.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import net.trackme.server.domain.Status;
import net.trackme.server.service.persistence.PersistenceService;

import java.util.UUID;

/**
 * @author Dimitri (22/01/2017)
 */
public class PersistenceServiceMongoImpl implements PersistenceService {

    private MongoClient mongo;

    private Vertx vertx;

    public PersistenceServiceMongoImpl(Vertx vertx) {
        this.vertx = vertx;

        // FIXME Externalize this
        JsonObject configuration = new JsonObject()
                .put("host", "localhost")
                .put("port", 27017)
                .put("db_name", "track-me");
        mongo = MongoClient.createShared(vertx, configuration);
    }

    @Override
    public void createTrip(JsonObject trip, Handler<AsyncResult<JsonObject>> resultHandler) {
        trip.put("ownershipToken", UUID.randomUUID().toString())
                .put("Status", Status.CREATED);

        mongo.save("trips", trip, asyncResult -> {
            if (asyncResult.succeeded()) {
                resultHandler.handle(Future.succeededFuture(trip.put("_id", asyncResult.result())));
            } else {
                resultHandler.handle(Future.failedFuture(asyncResult.cause()));
            }
        });
    }

    @Override
    public void read(String tripId, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject query = new JsonObject().put("_id", tripId);

        mongo.findOne("trips", query, null, asyncResult -> {
            if (asyncResult.succeeded()) {
                resultHandler.handle(Future.succeededFuture(asyncResult.result()));
            } else {
                resultHandler.handle(Future.failedFuture(asyncResult.cause()));
            }
        });
    }

    @Override
    public void update(JsonObject trip, Handler<AsyncResult<String>> resultHandler) {
        mongo.save("trips", trip, asyncResult -> {
            if (asyncResult.succeeded()) {
                resultHandler.handle(Future.succeededFuture(asyncResult.result()));
            } else {
                resultHandler.handle(Future.failedFuture(asyncResult.cause()));
            }
        });
    }

    @Override
    public void delete(String tripId, Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public void destroy() {
        mongo.close();
    }
}
