package net.trackme;

import io.vertx.core.Vertx;
import net.trackme.server.verticle.PersistenceVerticle;
import net.trackme.server.verticle.ServerVerticle;
import net.trackme.server.verticle.TripVerticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ServerApplication {

    @Value("${server.port}")
    private int serverPort;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@PostConstruct
    public void deployVerticles() {
	    final Vertx vertx = Vertx.vertx();
	    vertx.deployVerticle(new PersistenceVerticle());
	    vertx.deployVerticle(new TripVerticle());
	    vertx.deployVerticle(new ServerVerticle(serverPort));
    }
}
