package net.trackme.server.service.persistence.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import net.trackme.server.service.persistence.PersistenceService;

/**
 * @author Dimitri (22/01/2017)
 */
public class PersistenceServiceMongoImpl implements PersistenceService {

    private Vertx vertx;

    public PersistenceServiceMongoImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void create(JsonObject trip, Handler<AsyncResult<String>> resultHandler) {

    }

    @Override
    public void read(String tripId, Handler<AsyncResult<JsonObject>> resultHandler) {

    }

    @Override
    public void update(JsonObject trip, Handler<AsyncResult<String>> resultHandler) {

    }

    @Override
    public void delete(String tripId, Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public void destroy() {

    }
}
