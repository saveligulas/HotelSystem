package fhv.hotel.event.client;

import com.esotericsoftware.kryo.kryo5.minlog.Log;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.model.IEventModel;
import fhv.hotel.core.kryo.KryoSerializer;
import fhv.hotel.event.protocol.header.Header;
import fhv.hotel.event.protocol.header.Payload;
import fhv.hotel.event.utility.HexConverter;
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

    public Connection(NetSocket socket, boolean rolloutRequested, IReceiveByteMessage... receivers) {
        this.socket = socket;
        state = State.CONNECTED;
        socket.handler(this::handleIncomingData);
        if (receivers != null) {
            doSetup(rolloutRequested, receivers);
            this.receivers = Arrays.asList(receivers);
        }

    }

    private void doSetup(boolean rolloutRequested, IReceiveByteMessage[] receivers) {
        Buffer buffer = Buffer.buffer();
        if (rolloutRequested) {
            buffer.appendBytes(new byte[] {0x01, 0x00, 0x00}); // setting first byte to notify server to send rollout events
        } else {
            buffer.appendBytes(Header.EMPTY_HEADER);
        }

        for (IReceiveByteMessage receiver : receivers) {
            buffer.appendBytes(receiver.getEventTypeIds());
        }
        socket.write(buffer);
    }

    public void handleIncomingData(Buffer data) {
        Log.info("Received data: " + HexConverter.toHex(data.getBytes()));
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
