package fhv.hotel.event.client;

import fhv.hotel.core.event.IReceiveMessage;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.event.protocol.header.Header;
import fhv.hotel.event.protocol.header.Payload;
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
    private List<IReceiveByteMessage<?>> receivers;

    public Connection(NetSocket socket, IReceiveByteMessage<?>... receivers) {
        this.socket = socket;
        socket.handler(this::handleIncomingData);
        if (receivers != null) {
            doSetup(receivers);
        }
    }

    private void doSetup(IReceiveByteMessage<?>[] receivers) {
        Buffer buffer = Buffer.buffer();
        buffer.appendBytes(Header.EMPTY_HEADER);
        for (IReceiveByteMessage<?> receiver : receivers) {
            buffer.appendByte(receiver.getTypeByte());
        }
        socket.write(buffer);
    }

    public void handleIncomingData(Buffer data) {
        if (!state.equals(State.CONNECTED)) {
            throw new IllegalStateException("Connection is not connected and can not receive data yet");
        }

        byte eventType = Payload.getPublishType(data);

        for (IReceiveByteMessage<?> receiver : receivers) {
            if (receiver.getTypeByte() == eventType) {
                receiver.receiveAndConsume(Payload.getClassByteCode(data));
            }
        }
    }

}
