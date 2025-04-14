package fhv.hotel.command;

import fhv.hotel.core.event.EventListener;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class ApplicationConfig {
    @Produces
    @Singleton
    public EventListener eventListener() {
        EventListener eventListener = new EventListener();

        return eventListener;
    }

}
