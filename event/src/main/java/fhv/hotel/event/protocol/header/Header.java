package fhv.hotel.event.protocol.header;

@Deprecated
public class Header {
    public static final int HEADER_SIZE = 4;
    public static final byte[] EMPTY_HEADER = new byte[HEADER_SIZE];

    private FrameType type;
    private int channel;
    private int version;
}
