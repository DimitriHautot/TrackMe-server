package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.serviceproxy.ProxyHelper;
import net.trackme.server.domain.UpdateCommand;
import net.trackme.server.service.persistence.PersistenceService;
import net.trackme.server.service.persistence.mongo.PersistenceServiceMongoImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Verticle dedicated to the trip management.
 *
 * @author Dimitri (09/01/2017)
 */
public class ServerVerticle extends AbstractVerticle {

    private int serverPort;
    private PersistenceService persistenceService;

    public ServerVerticle(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/trip*").handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Content-Type")
        );
        router.post("/api/trip").handler(this::create);
        router.put("/api/trip/:tripId").handler(this::update);
        router.get("/api/trip/:tripId").handler(this::read);
        server.requestHandler(router::accept).listen(serverPort);

        final PersistenceServiceMongoImpl persistenceServiceMongo = new PersistenceServiceMongoImpl(vertx);
        ProxyHelper.registerService(PersistenceService.class, vertx, persistenceServiceMongo, PersistenceService.NAME);

        persistenceService = ProxyHelper.createProxy(PersistenceService.class, vertx, PersistenceService.NAME);

        startFuture.complete();
    }

    private void read(RoutingContext routingContext) {
        String tripId = routingContext.request().getParam("tripId");

        persistenceService.read(tripId, response -> {
            if (response.succeeded()) {
                if (response.result() != null) {
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200);
                    JsonObject tripJson = (JsonObject) response.result();
                    tripJson.remove("ownershipToken");
                    routingContext.response().end(Json.encodePrettily(tripJson));
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });

//        vertx.eventBus().send(Persistence.READ, tripId, asyncResult -> {
//            if (asyncResult.succeeded()) {
//                HttpServerResponse response = routingContext.response();
//                if (asyncResult.result().body() != null) {
//                    response.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200);
//                    JsonObject tripJson = (JsonObject) asyncResult.result().body();
//                    tripJson.remove("ownershipToken");
//                    response.end(Json.encodePrettily(tripJson));
//                } else {
//                    response.setStatusCode(404).end();
//                }
//            } else {
//                routingContext.response().setStatusCode(500).end();
//            }
//        });
    }

    private void create(RoutingContext routingContext) {
        vertx.eventBus().send(Persistence.CREATE, routingContext.getBodyAsJson(), asyncResult -> {
            if (asyncResult.succeeded()) {
                HttpServerResponse response = routingContext.response();
                response.putHeader("content-type", "application/json; charset=utf-8");
                if (asyncResult.result().body() != null) {
                    response.setStatusCode(201).end(Json.encodePrettily(asyncResult.result().body()));
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void update(RoutingContext routingContext) {
        String tripId = routingContext.request().getParam("tripId");
        String ownershipToken = routingContext.request().getHeader("Owner");

        JsonArray body = routingContext.getBodyAsJsonArray();
        List<UpdateCommand.UpdateCommandItem> items = new ArrayList<>(body.size());
        body.forEach(object -> {
            Object value;
            String name = ((JsonObject) object).getString("name");
            if ("geoLocation".equals(name)) {
                value = ((JsonObject) object).getJsonObject("value");
            } else {
                value = ((JsonObject) object).getString("value");
            }
            items.add(new UpdateCommand.UpdateCommandItem(name, value));
        });

        UpdateCommand command = UpdateCommand.builder()
                .tripId(tripId)
                .ownershipToken(ownershipToken)
                .items(items)
                .build();

        vertx.eventBus().send(TripVerticle.UPDATE, Json.encode(command), asyncResult -> {
            if (asyncResult.succeeded()) {
                HttpServerResponse response = routingContext.response();
                response.putHeader("content-type", "application/json; charset=utf-8");
                response.setStatusCode((Integer) asyncResult.result().body()).end();
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }
}
