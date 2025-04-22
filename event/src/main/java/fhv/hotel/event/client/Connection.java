package fhv.hotel.event.client;

import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.model.IEventModel;
import fhv.hotel.core.kryo.KryoSerializer;
import fhv.hotel.event.protocol.header.Header;
import fhv.hotel.event.protocol.header.Payload;
import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.Arrays;
import java.util.List;

class Connection {
    private enum State {
        SENDING_CONSUMER_TYPES,
        SENDING_PUBLISHER_TYPES,
        CONNECTED,
        CLOSED;
    }

    private final NetSocket socket;
    private final KryoSerializer serializer = new KryoSerializer();
    private State state;
    private List<IReceiveByteMessage> receivers;

    public Connection(NetSocket socket, IReceiveByteMessage... receivers) {
        this.socket = socket;
        state = State.CONNECTED;
        socket.handler(this::handleIncomingData);
        if (receivers != null) {
            doSetup(receivers);
            this.receivers = Arrays.asList(receivers);
        }

    }

    private void doSetup(IReceiveByteMessage[] receivers) {
        Buffer buffer = Buffer.buffer();
        buffer.appendBytes(Header.EMPTY_HEADER);
        for (IReceiveByteMessage receiver : receivers) {
            buffer.appendBytes(receiver.getEventTypeIds());
        }
        socket.write(buffer);
    }

    public void handleIncomingData(Buffer data) {
        if (!state.equals(State.CONNECTED)) {
            throw new IllegalStateException("Connection is not connected and can not receive data yet");
        }

        byte eventType = data.getByte(0); // published events have their header stripped

        for (IReceiveByteMessage receiver : receivers) {
            if (receiver.handlesType(eventType)) {
                receiver.receiveAndProcess(data.getBytes());
            }
        }
    }

    public void sendEvent(IEventModel model) {
        Buffer buffer = Buffer.buffer();
        buffer.appendBytes(Header.EMPTY_HEADER);
        buffer.appendByte(model.getEventType());
        buffer.appendBytes(serializer.serialize(model));
        socket.write(buffer);
    }
}
