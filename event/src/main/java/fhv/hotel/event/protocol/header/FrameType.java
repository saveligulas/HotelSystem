package fhv.hotel.event.protocol.header;

public enum FrameType {
    REGISTERING_CONSUMERS((byte) 1),
    PUBLISH((byte) 2),
    CONSUME((byte) 3);

    public final byte VALUE;

    FrameType(byte b) {
        VALUE = b;
    }
}
