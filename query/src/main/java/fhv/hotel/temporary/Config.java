package fhv.hotel.temporary;

import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.event.client.TCPClient;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
public class Config {
    @Inject
    Vertx vertx;
    @Inject
    IReceiveByteMessage receiver;

    @PostConstruct
    public void initClient() {
        TCPClient client = new TCPClient(vertx, receiver);
    }
}
