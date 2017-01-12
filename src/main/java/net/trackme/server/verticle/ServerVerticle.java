package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import net.trackme.server.domain.Trip;

/**
 * Verticle dedicated to the trip management.
 *
 * @author Dimitri (09/01/2017)
 */
public class ServerVerticle extends AbstractVerticle {

    private int serverPort;

    public ServerVerticle(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void start() throws Exception {
        super.start();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/trip*").handler(BodyHandler.create());
        router.post("/api/trip").handler(this::save);
        router.get("/api/trip/:tripId").handler(this::read);
        server.requestHandler(router::accept).listen(serverPort);
    }

    private void read(RoutingContext routingContext) {
        String tripId = routingContext.request().getParam("tripId");

        vertx.eventBus().send(Persistence.READ, tripId, asyncResult -> {
            if (asyncResult.succeeded()) {
                HttpServerResponse response = routingContext.response();
                if (asyncResult.result().body() != null) {
                    response.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200);
                    response.end(Json.encodePrettily(asyncResult.result().body()));
                } else {
                    response.setStatusCode(404).end();
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void save(RoutingContext routingContext) {
        vertx.eventBus().send(Persistence.SAVE, routingContext.getBodyAsJson(), asyncResult -> {
            if (asyncResult.succeeded()) {
                HttpServerResponse response = routingContext.response();
                response.putHeader("content-type", "application/json; charset=utf-8");
                if (asyncResult.result().body() != null) {
                    response.setStatusCode(201).end(Json.encodePrettily(asyncResult.result().body()));
                } else {
                    response.setStatusCode(204).end();
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }
}
