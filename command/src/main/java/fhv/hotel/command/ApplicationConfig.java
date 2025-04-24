package fhv.hotel.command;

import fhv.hotel.event.client.TCPClient;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class ApplicationConfig {
    @Inject
    Vertx vertx;

    @Produces
    @Singleton
    public TCPClient eventPublisher() {
        return new TCPClient(vertx);
    }

}
