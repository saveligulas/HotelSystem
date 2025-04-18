package fhv.hotel.event.server;

import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;

public class Connection {
    public enum State {
        INITIAL,
        CONSUMER_TYPES_RECEIVED,
        PUBLISHER_TYPES_RECEIVED,
        CHANNELS_RECEIVED,
        CONNECTED,
        CLOSED;
    }

    private final NetSocket socket;
    private State state;

    @Inject
    Publisher publisher;
    @Inject
    ConsumerRegistry consumerRegistry;


    public Connection(NetSocket socket) {
        this.socket = socket;
        state = State.INITIAL;
    }

    public void handleIncomingData(Buffer data) {
        Log.info("Received data: " + data);
        switch (state) {
            case INITIAL -> handleConsumerTypes(data);
            //case CONSUMER_TYPES_RECEIVED -> handlePublisherTypes(data);
            //case PUBLISHER_TYPES_RECEIVED -> handleChannelsReceived(data); //skipping this for now
            case CONNECTED -> handleDataFrame(data);
        }
    }

    private void handleDataFrame(Buffer data) {
    }

    private void handleConsumerTypes(Buffer data) {


        state = State.CONNECTED;
    }

    private void handlePublisherTypes(Buffer data) {
        state = State.CONNECTED; //change this to PUBLISHER_TYPES_RECEIVED once channels received is implemented correctly
    }

    private void handleChannelsReceived(Buffer data) {
        state = State.CONNECTED;
    }
}
