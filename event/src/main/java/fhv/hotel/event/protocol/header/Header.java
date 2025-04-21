package fhv.hotel.event.protocol.header;

public class Header {
    public static final int HEADER_SIZE = 3;
    public static final byte[] EMPTY_HEADER = new byte[HEADER_SIZE];
    private FrameType type;
    private int channel;
    private int version;
}
