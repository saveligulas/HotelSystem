package fhv.hotel.event.server;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Publisher {

    @Inject
    ConsumerRegistry registry;

    public void publish(Buffer data, Byte eventIdentifier) {
        for (NetSocket socket : registry.getSockets(eventIdentifier)) {
            socket.write(data);
        }
    }
}
