package net.trackme.server.service.persistence;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import net.trackme.server.service.persistence.mongo.PersistenceServiceMongoImpl;

/**
 * @author Dimitri (22/01/2017)
 */
@ProxyGen
//@VertxGen
public interface PersistenceService {

    String NAME = "persistence.service.name";

    static PersistenceService create(Vertx vertx) {
        return new PersistenceServiceMongoImpl(vertx);
    }

    static PersistenceService createProxy(Vertx vertx, String address) {
        return new PersistenceServiceVertxEBProxy(vertx, address);
    }

    void createTrip(JsonObject trip, Handler<AsyncResult<JsonObject>> resultHandler);
    void read(String tripId, Handler<AsyncResult<JsonObject>> resultHandler);
    void update(JsonObject trip, Handler<AsyncResult<String>> resultHandler);
    void delete(String tripId, Handler<AsyncResult<Void>> resultHandler);

    @ProxyClose
    void destroy();
}
