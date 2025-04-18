package fhv.hotel.event.protocol.header;

public class Header {
    public static final int HEADER_SIZE = 3;
    private FrameType type;
    private int channel;
    private int version;
}
