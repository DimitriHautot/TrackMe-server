package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
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

        router.route(HttpMethod.GET, "/trip/:tripId").handler(routingContext -> {
            String tripId = routingContext.request().getParam("tripId");
            // TODO Load from persistence
            // TODO return result as body, or 404
        });

        server.requestHandler(router::accept).listen(serverPort);
    }
}
