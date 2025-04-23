package fhv.hotel.event.server;

import fhv.hotel.event.protocol.header.Frame;
import fhv.hotel.event.protocol.header.FrameType;
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
        CONNECTED,
        CLOSED
    }

    private final NetSocket socket;
    private final Vertx vertx;
    private State state;
    private ConsumerRegistry consumerRegistry;
    private Publisher publisher;
    private IEventSourcingRepository eventSourcingRepository;

    public Connection(NetSocket socket, Vertx vertx, IEventSourcingRepository eventSourcingRepository, 
                     ConsumerRegistry consumerRegistry, Publisher publisher) {
        this.socket = socket;
        this.vertx = vertx;
        this.state = State.INITIAL;
        this.eventSourcingRepository = eventSourcingRepository;
        this.consumerRegistry = consumerRegistry;
        this.publisher = publisher;
    }

    public void handleIncomingData(Buffer data) {
        Log.info("Received data: " + HexConverter.toHex(data.getBytes()));
        
        List<Frame> frames = Frame.splitBuffer(data);
        for (Frame frame : frames) {
            processFrame(frame);
        }
    }
    
    private void processFrame(Frame frame) {
        switch (state) {
            case INITIAL:
                if (frame.getType() == FrameType.REGISTERING_CONSUMERS) {
                    handleConsumerRegistration(frame);
                }
                break;
            case CONNECTED:
                if (frame.getType() == FrameType.PUBLISH) {
                    handlePublishFrame(frame);
                }
                break;
        }
    }

    private void handlePublishFrame(Frame frame) {
        byte[] payload = frame.getPayloadBytes();
        if (payload.length == 0) {
            return;
        }

        byte eventType = payload[0];

        vertx.executeBlocking(() -> {
                eventSourcingRepository.saveByteEvent(eventType, payload);
                return null;
            })
            .onSuccess(result -> {
                publisher.publish(Buffer.buffer(payload), eventType);
            })
            .onFailure(error -> {
                Log.error("Failed to save event", error);
            });
    }

    private void handleConsumerRegistration(Frame frame) {
        state = State.CONNECTED;
        byte[] payload = frame.getPayloadBytes();
        
        // Check second byte in header (position 1) for rollout request flag
        boolean rolloutRequested = frame.getBuffer().getByte(1) == 0x01;
        
        Log.info("Consumer registration frame received. Rollout requested: " + rolloutRequested + 
                ", Payload length: " + payload.length);

        List<Byte> registeredTypes = new ArrayList<>();
        for (byte b : payload) {
            consumerRegistry.add(b, this.socket);
            registeredTypes.add(b);
            Log.info("Registered consumer for event type: " + b);
        }

        if (rolloutRequested) {
            handleRollout(registeredTypes);
        }
    }

    private void handleRollout(List<Byte> typeIdentifiers) {
        Log.info("Starting rollout for " + typeIdentifiers.size() + " event types");
        
        for (Byte eventType : typeIdentifiers) {
            processEventsForType(eventType);
        }
    }

    private void processEventsForType(byte eventType) {
        Log.info("Processing rollout for event type: " + eventType);

        vertx.executeBlocking(() -> {
                return eventSourcingRepository.getByteEventsByTypeAscending(eventType);
            })
            .onSuccess(events -> {
                Log.info("Retrieved " + events.size() + " events for type " + eventType);
                
                for (byte[] eventData : events) {
                    sendRolloutEvent(eventType, eventData);
                }
            })
            .onFailure(error -> {
                Log.error("Failed to retrieve events for type: " + eventType, error);
            });
    }
    
    private void sendRolloutEvent(byte eventType, byte[] eventData) {
        Log.info("Sending rollout event of type: " + eventType + ", data length: " + eventData.length);
        
        Frame frame = Frame.builder()
                .setType(FrameType.CONSUME)
                .setPayload(eventData)
                .build();
        
        Log.debug("Sending frame: " + HexConverter.toHex(frame.getBytes()));
        
        // Send the frame
        socket.write(frame.getBuffer());
    }
}
