package net.trackme.server.verticle;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.serviceproxy.ProxyHelper;
import lombok.extern.slf4j.Slf4j;
import net.trackme.server.service.trip.TripService;

/**
 * @author Dimitri (26/01/2017)
 */
@Slf4j
public class BridgeEventHandler implements Handler<BridgeEvent> {

    private TripService tripService;

    public BridgeEventHandler(Vertx vertx) {
        tripService = ProxyHelper.createProxy(TripService.class, vertx, TripService.NAME);
    }

    public void stop() {
        tripService = null;
    }

    @Override
    public void handle(BridgeEvent bridgeEvent) {
        switch (bridgeEvent.type()) {
            case REGISTER:
                String address = bridgeEvent.getRawMessage().getString("address");
                // If the address refers to an update address of a particular Trip ID, check if it (still) exist
                if (address.matches("event\\.trip\\.update\\..+")) {
                    String tripId = address.substring(address.lastIndexOf('.'));
                    tripService.exist(tripId, response -> {
                        bridgeEvent.complete(response.succeeded());
                    });
                    break;
                }

                // Otherwise, simply reject the event
                bridgeEvent.complete(false);
                break;

            case RECEIVE:
                log.info("Delivering message to clients: {}", bridgeEvent.getRawMessage());
                break;
            default:
                log.info("Bridging this message: {}", bridgeEvent.getRawMessage());
                break;
        }
    }
}
