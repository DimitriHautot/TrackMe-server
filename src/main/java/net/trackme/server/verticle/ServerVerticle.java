package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

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
        EventBus eventBus = vertx.eventBus();

        router.route(HttpMethod.GET, "/trip/:tripId").handler(routingContext -> {
            String tripId = routingContext.request().getParam("tripId");
            eventBus.send(PersistenceVerticle.READ, tripId, asyncResult -> {
               if (asyncResult.succeeded()) {
                   HttpServerResponse response = routingContext.response();
                   if (asyncResult.result().body() != null) {
                       response.putHeader("content-type", "application/json; charset=utf-8");
                       response.setStatusCode(200);
                       response.end(Json.encodePrettily(asyncResult.result()));
                   } else {
                       response.setStatusCode(404);
                       response.end();
                   }
               }
            });
        });

        server.requestHandler(router::accept).listen(serverPort);
    }
}
