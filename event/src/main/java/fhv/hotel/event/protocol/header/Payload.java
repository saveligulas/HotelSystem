package fhv.hotel.event.protocol.header;

import io.vertx.core.buffer.Buffer;

public class Payload {
    public static byte getPublishType(Buffer buffer) {
        return buffer.getByte(Header.HEADER_SIZE);
    }

    public static byte[] getClassByteCode(Buffer buffer) {
        byte[] bufferBytes = buffer.getBytes();
        byte[] classByteCode = new byte[bufferBytes.length - Header.HEADER_SIZE - 1];
        System.arraycopy(bufferBytes, Header.HEADER_SIZE, classByteCode, 0, classByteCode.length);
        return classByteCode;
    }
}
