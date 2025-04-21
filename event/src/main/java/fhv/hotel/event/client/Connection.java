package fhv.hotel.event.client;

import fhv.hotel.event.protocol.header.Header;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.List;
import java.util.Map;

class Connection {
    private enum State {
        SENDING_CONSUMER_TYPES,
        SENDING_PUBLISHER_TYPES,
        CONNECTED,
        CLOSED;
    }

    private final NetSocket socket;
    private State state;

    public Connection(NetSocket socket, byte[] consumerClassIdentifiers) {
        this.socket = socket;
        socket.handler(this::handleIncomingData);
        if (consumerClassIdentifiers != null) {
            doSetup(consumerClassIdentifiers);
        }

    }

    private void doSetup(byte[] consumerClassIdentifiers) {
        Buffer buffer = Buffer.buffer();
        buffer.appendBytes(Header.EMPTY_HEADER);
        buffer.appendBytes(consumerClassIdentifiers);
        socket.write(buffer);
    }

    public void handleIncomingData(Buffer data) {
        if (!state.equals(State.CONNECTED)) {
            throw new IllegalStateException("Connection is not connected and can not receive data yet");
        }
    }

}
