package net.trackme.server.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.serviceproxy.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import net.trackme.server.service.persistence.PersistenceService;
import net.trackme.server.service.persistence.mongo.PersistenceServiceMongoImpl;
import net.trackme.server.service.trip.TripService;
import net.trackme.server.service.trip.impl.TripServiceImpl;

/**
 * Verticle dedicated to the trip management.
 *
 * @author Dimitri (09/01/2017)
 */
@Slf4j
public class ServerVerticle extends AbstractVerticle {

    private int serverPort;
    private TripService tripService;
    private PersistenceService persistenceService;
    private BridgeEventHandler bridgeEventHandler;
    private MessageConsumer<JsonObject> persistenceMessageConsumer;
    private MessageConsumer<JsonObject> tripMessageConsumer;

    public ServerVerticle(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        // Services instances & proxies
        final PersistenceServiceMongoImpl persistenceServiceMongo = new PersistenceServiceMongoImpl(vertx);
        persistenceMessageConsumer = ProxyHelper.registerService(PersistenceService.class, vertx, persistenceServiceMongo, PersistenceService.NAME);
        final TripServiceImpl tripServiceImpl = new TripServiceImpl(vertx);
        tripMessageConsumer = ProxyHelper.registerService(TripService.class, vertx, tripServiceImpl, TripService.NAME);

        persistenceService = ProxyHelper.createProxy(PersistenceService.class, vertx, PersistenceService.NAME);
        tripService = ProxyHelper.createProxy(TripService.class, vertx, TripService.NAME);

        // HTTP server
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/trip*").handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Content-Type")
        );
        router.post("/api/trip").handler(this::create);
        router.patch("/api/trip/:tripId").handler(this::update);
        router.get("/api/trip/:tripId").handler(this::read);

        // SockJS transport
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        PermittedOptions outboundPermitted = new PermittedOptions().setAddressRegex("event\\.trip\\.update\\..+");
        BridgeOptions bridgeOptions = new BridgeOptions().addOutboundPermitted(outboundPermitted);
        bridgeEventHandler = new BridgeEventHandler(vertx);
        sockJSHandler.bridge(bridgeOptions, bridgeEventHandler);
        router.route("/eventbus/*").handler(sockJSHandler);

        vertx.eventBus().consumer("test", message -> {
            log.info("/ ... {}", message);
        });
        vertx.eventBus().consumer("event.trip.update.*", message -> {
            log.info("// ... {}", message);
        });

        server.requestHandler(router::accept).listen(serverPort);
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        bridgeEventHandler.stop();
        bridgeEventHandler = null;
        tripService = null;
        persistenceService = null;
        ProxyHelper.unregisterService(tripMessageConsumer);
        ProxyHelper.unregisterService(persistenceMessageConsumer);

        stopFuture.complete();
    }

    private void read(RoutingContext routingContext) {
        String tripId = routingContext.request().getParam("tripId");

        persistenceService.read(tripId, response -> {
            if (response.succeeded()) {
                if (response.result() != null) {
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200);
                    JsonObject tripJson = response.result();
                    tripJson.remove("ownershipToken");
                    routingContext.response().end(Json.encodePrettily(tripJson));
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void create(RoutingContext routingContext) {
        persistenceService.createTrip(routingContext.getBodyAsJson(), response -> {
            if (response.succeeded()) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8");
                if (response.result() != null) {
                    routingContext.response().setStatusCode(201).end(Json.encodePrettily(response.result()));
                }
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }

    private void update(RoutingContext routingContext) {
        tripService.update(
                routingContext.request().getParam("tripId"),
                routingContext.request().getHeader("Owner"),
                routingContext.getBodyAsJsonArray(),
                response ->
        {
            if (response.succeeded()) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8");
                routingContext.response().setStatusCode((Integer) response.result()).end();
            } else {
                routingContext.response().setStatusCode(500).end();
            }
        });
    }
}
