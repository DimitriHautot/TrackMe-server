package net.trackme.server.service.trip;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import net.trackme.server.service.trip.impl.TripServiceImpl;

/**
 * @author Dimitri (24/01/2017)
 */
@ProxyGen
public interface TripService {

    String NAME = "service.trip";

    static TripService create(Vertx vertx) {
        return new TripServiceImpl(vertx);
    }

    static TripService createProxy(Vertx vertx, String address) {
        return new TripServiceVertxEBProxy(vertx, address);
    }

    void update(String tripId, String ownershipToken, JsonArray updateItemsJA, Handler<AsyncResult<Integer>> resultHandler);
    void exist(String tripId, Handler<AsyncResult<Boolean>> resultHandler);

    @ProxyClose
    void destroy();
}
