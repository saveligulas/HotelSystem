package fhv.hotel.event.client;

import com.esotericsoftware.kryo.kryo5.minlog.Log;
import fhv.hotel.core.event.bytebased.IReceiveByteMessage;
import fhv.hotel.core.model.IEventModel;
import fhv.hotel.core.kryo.KryoSerializer;
import fhv.hotel.event.protocol.header.Frame;
import fhv.hotel.event.protocol.header.FrameType;
import fhv.hotel.event.utility.HexConverter;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.Arrays;
import java.util.List;

class Connection {
    private enum State {
        CONNECTED,
        CLOSED
    }

    private final NetSocket socket;
    private final Vertx vertx;
    private final KryoSerializer serializer = new KryoSerializer();
    private State state;
    private List<IReceiveByteMessage> receivers;

    public Connection(NetSocket socket, Vertx vertx, boolean rolloutRequested, IReceiveByteMessage... receivers) {
        this.socket = socket;
        this.vertx = vertx;
        this.state = State.CONNECTED;
        socket.handler(this::handleIncomingData);
        if (receivers != null) {
            doSetup(rolloutRequested, receivers);
            this.receivers = Arrays.asList(receivers);
        }
    }

    private void doSetup(boolean rolloutRequested, IReceiveByteMessage[] receivers) {
        byte[] payload = new byte[0];
        for (IReceiveByteMessage receiver : receivers) {
            byte[] eventTypeIds = receiver.getEventTypeIds();
            byte[] newPayload = new byte[payload.length + eventTypeIds.length];
            
            System.arraycopy(payload, 0, newPayload, 0, payload.length);
            System.arraycopy(eventTypeIds, 0, newPayload, payload.length, eventTypeIds.length);
            
            payload = newPayload;
        }
        
        Frame.Builder frameBuilder = Frame.builder()
                .setType(FrameType.REGISTERING_CONSUMERS)
                .setPayload(payload);
                
        Frame frame = frameBuilder.build();
        
        if (rolloutRequested) {
            // Set the second byte in the header to 0x01 to indicate rollout request
            frame.getBuffer().setByte(1, (byte) 0x01);
        }
        
        Log.info("Sending registration frame: " + HexConverter.toHex(frame.getBytes()));
        socket.write(frame.getBuffer());
    }

    public void handleIncomingData(Buffer data) {
        Log.info("Received data: " + HexConverter.toHex(data.getBytes()));
        if (state != State.CONNECTED) {
            throw new IllegalStateException("Connection is not connected and cannot receive data yet");
        }

        List<Frame> frames = Frame.splitBuffer(data);
        Log.info("Split into " + frames.size() + " frames");
        
        for (Frame frame : frames) {
            Log.info("Processing frame type: " + frame.getType() + ", size: " + frame.getSize() + 
                     ", payload size: " + frame.getPayloadBytes().length);
            
            if (frame.getType() == FrameType.CONSUME) {
                processConsumeFrame(frame);
            }
        }
    }

    private void processConsumeFrame(Frame frame) {
        byte[] payload = frame.getPayloadBytes();
        if (payload.length == 0) {
            Log.warn("Received empty payload in CONSUME frame");
            return;
        }

        byte eventType = payload[0];
        Log.info("Received event of type: " + eventType + " with payload length: " + payload.length);

        Log.info("Received event: " + HexConverter.toHex(payload));

        if (payload.length <= 1) {
            Log.warn("Payload too short: " + HexConverter.toHex(payload));
            return;
        }
        
        for (IReceiveByteMessage receiver : receivers) {
            if (receiver.handlesType(eventType)) {
                final byte[] eventData = payload;
                final IReceiveByteMessage eventReceiver = receiver;
                
                vertx.executeBlocking(() -> {
                    try {
                        Log.info("Processing event of type: " + eventType + " in worker thread");
                        eventReceiver.receiveAndProcess(eventData);
                        return true;
                    } catch (Exception e) {
                        Log.error("Error processing event", e);
                        return false;
                    }
                })
                .onSuccess(result -> {
                    Log.info("Successfully processed event of type: " + eventType);
                })
                .onFailure(error -> {
                    Log.error("Failed to process event of type: " + eventType, error);
                });
                
                return; // Process with the first matching receiver
            }
        }
        
        Log.warn("No handler found for event type: " + eventType);
    }

    public void sendEvent(IEventModel model) {
        byte[] serializedModel = serializer.serialize(model);

        byte[] payload = new byte[1 + serializedModel.length];
        payload[0] = model.getEventType();
        System.arraycopy(serializedModel, 0, payload, 1, serializedModel.length);
        
        Frame frame = Frame.builder()
                .setType(FrameType.PUBLISH)
                .setPayload(payload)
                .build();
                
        Log.info("Sending event frame: " + HexConverter.toHex(frame.getBytes()));
        socket.write(frame.getBuffer());
    }
}
