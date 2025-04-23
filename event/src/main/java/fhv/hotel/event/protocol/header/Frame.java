package fhv.hotel.event.protocol.header;

import fhv.hotel.event.utility.ByteConverter;
import fhv.hotel.event.utility.HexConverter;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    public static final byte END_FRAME = (byte) 0xFF;
    public static final int HEADER_SIZE = 8;
    
    private static final int TYPE_POSITION = 0;
    private static final int SIZE_MSB_POSITION = 6;
    private static final int SIZE_LSB_POSITION = 7;

    private final Buffer buffer;

    private Frame() {
        this.buffer = Buffer.buffer();
    }

    private Frame(Buffer buffer) {
        this.buffer = buffer;
    }

    public static Frame fromBytes(byte[] bytes) {
        return new Frame(Buffer.buffer(bytes));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Frame createRegisteringConsumersFrame(byte[] payload) {
        return builder()
                .setType(FrameType.REGISTERING_CONSUMERS)
                .setPayload(payload)
                .build();
    }

    public static Frame createPublishFrame(byte[] payload) {
        return builder()
                .setType(FrameType.PUBLISH)
                .setPayload(payload)
                .build();
    }

    public static Frame createConsumeFrame(byte[] payload) {
        return builder()
                .setType(FrameType.CONSUME)
                .setPayload(payload)
                .build();
    }

    public FrameType getType() {
        byte typeValue = buffer.getByte(TYPE_POSITION);
        for (FrameType type : FrameType.values()) {
            if (type.VALUE == typeValue) {
                return type;
            }
        }
        throw new IllegalStateException("Unknown frame type: " + typeValue);
    }

    public int getSize() {
        return ByteConverter.toShort(buffer.getByte(SIZE_MSB_POSITION), buffer.getByte(SIZE_LSB_POSITION));
    }

    public int getPayloadSize() {
        int size = getSize();
        return Math.max(0, size - HEADER_SIZE - 1);
    }

    public Buffer getPayload() {
        try {
            int size = getSize();

            if (size <= HEADER_SIZE + 1) {
                return Buffer.buffer();
            }

            int payloadLength = size - HEADER_SIZE - 1;

            if (payloadLength <= 0 || HEADER_SIZE + payloadLength > buffer.length()) {
                return Buffer.buffer();
            }

            Buffer payloadBuffer = Buffer.buffer(payloadLength);

            for (int i = 0; i < payloadLength; i++) {
                payloadBuffer.appendByte(buffer.getByte(HEADER_SIZE + i));
            }
            
            return payloadBuffer;
        } catch (Exception e) {
            System.err.println("Error extracting payload: " + e.getMessage() + " from buffer: " + 
                              HexConverter.toHex(buffer.getBytes()));
            return Buffer.buffer();
        }
    }

    public byte[] getPayloadBytes() {
        Buffer payload = getPayload();
        return payload != null && payload.length() > 0 ? payload.getBytes() : new byte[0];
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public byte[] getBytes() {
        return buffer.getBytes();
    }

    public void validate() {
        if (buffer.length() == 0 || buffer.getByte(buffer.length() - 1) != END_FRAME) {
            buffer.appendByte(END_FRAME);
        }
        
        if (getSize() != buffer.length()) {
            setSize(buffer.length());
        }
    }

    private void setSize(int size) {
        byte[] bytes = ByteConverter.fromShort(size);
        buffer.setByte(SIZE_MSB_POSITION, bytes[0]);
        buffer.setByte(SIZE_LSB_POSITION, bytes[1]);
    }
    
    public static List<Frame> splitBuffer(Buffer buffer) {
        List<Frame> frames = new ArrayList<>();
        int position = 0;

        while (position + HEADER_SIZE <= buffer.length()) {
            try {
                int frameSize = ByteConverter.toShort(
                        buffer.getByte(position + SIZE_MSB_POSITION),
                        buffer.getByte(position + SIZE_LSB_POSITION)
                );

                if (frameSize <= 0) {
                    position++; // skip to check for next valid frame
                    continue;
                }

                if (position + frameSize > buffer.length()) {
                    break;
                }

                if (buffer.getByte(position + frameSize - 1) != END_FRAME) {
                    position++;
                    continue;
                }

                Buffer frameBuffer = Buffer.buffer(frameSize);
                for (int i = 0; i < frameSize; i++) {
                    frameBuffer.appendByte(buffer.getByte(position + i));
                }
                
                frames.add(new Frame(frameBuffer));
                position += frameSize;
            } catch (Exception e) {
                System.err.println("Error splitting buffer at position " + position + ": " + 
                                  e.getMessage());
                position++;
            }
        }
        
        return frames;
    }

    public static boolean containsMultipleFrames(Buffer buffer) {
        if (buffer.length() < HEADER_SIZE) {
            return false;
        }
        
        try {
            int firstFrameSize = ByteConverter.toShort(
                    buffer.getByte(SIZE_MSB_POSITION),
                    buffer.getByte(SIZE_LSB_POSITION)
            );
            
            return firstFrameSize > 0 && buffer.length() > firstFrameSize;
        } catch (Exception e) {
            return false;
        }
    }

    public static class Builder {
        private final Buffer buffer;
        private FrameType type;
        private byte[] payload;

        private Builder() {
            this.buffer = Buffer.buffer();
            this.type = FrameType.PUBLISH;
        }

        public Builder setType(FrameType type) {
            this.type = type;
            return this;
        }

        public Builder setPayload(byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Builder setPayload(Buffer payloadBuffer) {
            this.payload = payloadBuffer != null ? payloadBuffer.getBytes() : null;
            return this;
        }

        public Frame build() {
            buffer.setBytes(0, new byte[HEADER_SIZE]);
            buffer.setByte(TYPE_POSITION, type.VALUE);
            
            if (payload != null && payload.length > 0) {
                buffer.appendBytes(payload);
            }
            
            buffer.appendByte(END_FRAME);
            
            byte[] sizeBytes = ByteConverter.fromShort(buffer.length());
            buffer.setByte(SIZE_MSB_POSITION, sizeBytes[0]);
            buffer.setByte(SIZE_LSB_POSITION, sizeBytes[1]);
            
            return new Frame(buffer);
        }
    }
}
