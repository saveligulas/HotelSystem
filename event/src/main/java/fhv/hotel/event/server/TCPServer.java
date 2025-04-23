package fhv.hotel.event.server;

import fhv.hotel.event.repo.IEventSourcingRepository;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Startup
public class TCPServer {
    private final Map<NetSocket, Connection> connections = new HashMap<>();
    @Inject
    Vertx vertx;
    @Inject
    IEventSourcingRepository eventSourcingRepository;
    @Inject
    Publisher publisher;
    @Inject
    ConsumerRegistry consumerRegistry;


    @PostConstruct
    public void start() {
        NetServer server = vertx.createNetServer();
        server.connectHandler(this::handleNewConnection);
        server.listen(5672, res -> {
            if (res.succeeded()) {
                Log.info("TCP Server running on port 5672");
            } else {
                Log.error("Failed to start TCP Server: {}", res.cause());
            }
        });
    }

    private void handleNewConnection(NetSocket socket) {
        Connection connection = new Connection(socket, vertx, eventSourcingRepository, consumerRegistry, publisher);
        connections.put(socket, connection);

        socket.handler(buffer -> {
            Connection connectionToSend = connections.get(socket);
            if (connectionToSend != null) {
                connectionToSend.handleIncomingData(buffer);
            }
        });

        socket.closeHandler(v -> {
            connections.remove(socket);
            Log.info("Connection closed with " + socket.remoteAddress());
        });
    }

}
