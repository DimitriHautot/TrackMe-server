package net.trackme.server.service.trip.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;
import net.trackme.server.domain.GeoLocation;
import net.trackme.server.domain.Status;
import net.trackme.server.domain.Trip;
import net.trackme.server.domain.UpdateItem;
import net.trackme.server.service.persistence.PersistenceService;
import net.trackme.server.service.trip.TripService;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible to hold business logic related to {@link net.trackme.server.domain.Trip} instances.
 *
 * @author Dimitri (24/01/2017)
 */
public class TripServiceImpl implements TripService {

    private Vertx vertx;
    private PersistenceService persistenceService;

    public TripServiceImpl(Vertx vertx) {
        this.vertx = vertx;
        persistenceService = ProxyHelper.createProxy(PersistenceService.class, vertx, PersistenceService.NAME);
    }

    /**
     * <ol>
     *     <li>make sure the {@code ownershipToken} is valid for the trip</li>
     *     <li>apply the changes</li>
     *     <li>persist the updated trip</li>
     *     <li>return success to caller</li>
     * </ol>
     */
    @Override
    public void update(String tripId, String ownershipToken, JsonArray updateItemsJA, Handler<AsyncResult<Integer>> resultHandler) {

        persistenceService.read(tripId, response -> {
            if (response.succeeded()) {
                Trip trip = Json.decodeValue(response.result().toString(), Trip.class);
                if (!trip.getOwnershipToken().equals(ownershipToken)) {
                    resultHandler.handle(Future.succeededFuture(403));
                }
                apply(trip, updateItemsJA);

                persistenceService.update(new JsonObject(Json.encode(trip)), response2 -> {
                    resultHandler.handle(Future.succeededFuture(204));
                });
            }
        });
    }

    @Override
    public void destroy() {
        persistenceService = null;
    }

    private void apply(Trip trip, JsonArray updateItemsJA) {
        List<UpdateItem> items = instantiate(updateItemsJA);

        items.forEach(item -> {
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
                        trip.getGeoLocations().add(new GeoLocation((JsonObject) item.getValue()));
                    }
                    break;
            }
        });
    }

    private List<UpdateItem> instantiate(JsonArray updateItems) {
        List<UpdateItem> items = new ArrayList<>(updateItems.size());
        updateItems.forEach(object -> {
            Object value;
            String name = ((JsonObject) object).getString("name");
            if ("geoLocation".equals(name)) {
                value = ((JsonObject) object).getJsonObject("value");
            } else {
                value = ((JsonObject) object).getString("value");
            }
            items.add(new UpdateItem(name, value));
        });
        return items;
    }
}
