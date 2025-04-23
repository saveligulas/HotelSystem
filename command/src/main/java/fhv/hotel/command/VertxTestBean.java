package fhv.hotel.command;

import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
public class VertxTestBean {

    @Inject
    Vertx vertx;

    @PostConstruct
    public void log() {
        System.out.println("Vertx instance: " + vertx);
    }
}
