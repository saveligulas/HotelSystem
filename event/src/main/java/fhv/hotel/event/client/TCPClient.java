package fhv.hotel.event.client;

import com.esotericsoftware.kryo.kryo5.minlog.Log;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.model.IEventModel;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.util.LinkedList;
import java.util.Queue;

public class TCPClient implements IPublishEvent {
    private Connection connection;
    private boolean ready = false;
    private final Queue<IEventModel> pendingEvents = new LinkedList<>();

    public TCPClient(Vertx vertx, IReceiveByteMessage... receivers) {
        start(vertx, false, receivers);
    }

    public TCPClient(Vertx vertx, boolean rolloutRequested, IReceiveByteMessage... receivers) {
        start(vertx, rolloutRequested, receivers);
    }

    void start(Vertx vertx, boolean rolloutRequested, IReceiveByteMessage... receivers) {
        NetClient client = vertx.createNetClient();
        client.connect(5672, "localhost", conn -> {
            if (conn.succeeded()) {
                this.connection = new Connection(conn.result(), vertx, rolloutRequested, receivers);
                this.ready = true;
                while (!this.pendingEvents.isEmpty()) {
                    connection.sendEvent(pendingEvents.poll());
                }
            } else {
                Log.info("Connection could not be established: " + conn.cause());
            }
        });
    }

    @Override
    public <T extends IEventModel> void publish(T event) {
        if (ready) {
            connection.sendEvent(event);
        } else {
            pendingEvents.add(event);
        }
    }
}
