package fhv.hotel.event.server;

import fhv.hotel.event.protocol.header.Payload;
import fhv.hotel.event.repo.IEventSourcingRepository;
import fhv.hotel.event.utility.HexConverter;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;

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
    private final Vertx vertx;
    private State state;
    private ConsumerRegistry consumerRegistry;
    private Publisher publisher;
    private IEventSourcingRepository eventSourcingRepository;


    public Connection(NetSocket socket, Vertx vertx, IEventSourcingRepository eventSourcingRepository, ConsumerRegistry consumerRegistry, Publisher publisher) {
        this.socket = socket;
        this.vertx = vertx;
        this.state = State.INITIAL;
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

        vertx.executeBlocking(() -> {
                    // This code runs on a worker thread
                    eventSourcingRepository.saveByteEvent(identifier, payload);
                    return null;
                })
                .onSuccess(result -> {
                    // This runs on the event loop
                    publisher.publish(Buffer.buffer(payload), identifier);
                })
                .onFailure(error -> {
                    Log.error("Failed to save event", error);
                });
    }

    private void handleConsumerTypes(Buffer data) {
        state = State.CONNECTED;
        boolean rolloutRequested = false;
        if (data.getByte(0) == 0x01) { // if first byte is set, consumer requests event rollout
            rolloutRequested = true;
        }

        byte[] payload = Payload.getPayload(data);
        for (byte b : payload) {
            consumerRegistry.add(b, this.socket);
        }

        if (rolloutRequested) {
            handleRollout(payload);
        }
    }

    private void handleRollout(byte[] payload) {
        List<Byte> typeIdentifiers = new ArrayList<>();
        for (byte b : payload) {
            typeIdentifiers.add(b);
        }

        processNextIdentifier(typeIdentifiers, 0);
    }

    private void processNextIdentifier(List<Byte> identifiers, int index) {
        if (index >= identifiers.size()) {
            return;
        }

        Byte currentIdentifier = identifiers.get(index);

        vertx.executeBlocking(() -> {
                    return eventSourcingRepository.getByteEventsByTypeAscending(currentIdentifier);
                })
                .onSuccess(events -> {
                    for (byte[] event : events) {
                        Log.info("Writing Rollout event: " + HexConverter.toHex(event));
                        socket.write(Buffer.buffer(event));
                    }

                    processNextIdentifier(identifiers, index + 1);
                })
                .onFailure(error -> {
                    Log.error("Failed to retrieve events for type: " + currentIdentifier, error);
                    // Continue with next identifier despite error
                    processNextIdentifier(identifiers, index + 1);
                });
    }

    private void handlePublisherTypes(Buffer data) {
        state = State.CONNECTED; //change this to PUBLISHER_TYPES_RECEIVED once channels received is implemented correctly
    }

    private void handleChannelsReceived(Buffer data) {
        state = State.CONNECTED;
    }
}
