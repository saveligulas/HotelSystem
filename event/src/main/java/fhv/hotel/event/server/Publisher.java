package fhv.hotel.event.server;

import fhv.hotel.event.protocol.header.Frame;
import fhv.hotel.event.protocol.header.FrameType;
import fhv.hotel.event.utility.HexConverter;
import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Publisher {

    @Inject
    ConsumerRegistry registry;

    public void publish(Buffer data, Byte eventIdentifier) {
        byte[] eventData = data.getBytes();
        Log.debug("Publishing event type: " + eventIdentifier + 
                 ", data length: " + eventData.length);
        
        Frame frame = Frame.builder()
                .setType(FrameType.CONSUME)
                .setPayload(eventData)
                .build();
        
        for (NetSocket socket : registry.getSockets(eventIdentifier)) {
            socket.write(frame.getBuffer());
        }
    }
}
