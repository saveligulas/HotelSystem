package fhv.hotel.event.server;

import fhv.hotel.event.protocol.header.Header;
import fhv.hotel.event.protocol.header.Payload;
import fhv.hotel.event.utility.HexConverter;
import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

class Connection {
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
    private ConsumerRegistry consumerRegistry;
    private Publisher publisher;
    private IEventSourcingRepository eventSourcingRepository;


    public Connection(NetSocket socket, IEventSourcingRepository eventSourcingRepository, ConsumerRegistry consumerRegistry, Publisher publisher) {
        this.socket = socket;
        state = State.INITIAL;
        this.eventSourcingRepository = eventSourcingRepository;
        this.consumerRegistry = consumerRegistry;
        this.publisher = publisher;
    }

    public void handleIncomingData(Buffer data) {
        Log.info("Received data: " + HexConverter.toHex(data.getBytes()));
        switch (state) {
            case INITIAL -> handleConsumerTypes(data);
            //case CONSUMER_TYPES_RECEIVED -> handlePublisherTypes(data);
            //case PUBLISHER_TYPES_RECEIVED -> handleChannelsReceived(data); //skipping this for now
            case CONNECTED -> handleDataFrame(data);
        }
    }

    private void handleDataFrame(Buffer data) {
        Byte identifier = Payload.getPublishType(data);
        byte[] payload = Payload.getPayload(data);
        byte[] classByteCode = Payload.getClassByteCode(data);

        eventSourcingRepository.saveByteEvent(identifier, classByteCode);
        publisher.publish(Buffer.buffer(payload), identifier);
    }

    private void handleConsumerTypes(Buffer data) {
        state = State.CONNECTED;
        byte[] payload = Payload.getPayload(data);
        for (byte b : payload) {
            consumerRegistry.add(b, this.socket);
        }
    }

    private void handlePublisherTypes(Buffer data) {
        state = State.CONNECTED; //change this to PUBLISHER_TYPES_RECEIVED once channels received is implemented correctly
    }

    private void handleChannelsReceived(Buffer data) {
        state = State.CONNECTED;
    }
}
