package fhv.hotel.event.client;

import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class TCPClient {
    @Inject
    Vertx vertx;
    private Connection connection;

    public TCPClient(IReceiveByteMessage<?>... receivers) {
        start(receivers);
    }

    public void start(IReceiveByteMessage<?>... receivers) {
        NetClient client = vertx.createNetClient();
        client.connect(5672, "localhost", conn -> {
            if (conn.succeeded()) {
                this.connection = new Connection(conn.result(), receivers);
            } else {
                Log.info("Connection could not be established: " + conn.cause());
            }
        });
    }
}
