package fhv.hotel.event.utility;

import java.nio.charset.StandardCharsets;

@Deprecated // moved to Hex representation
public final class Utf8Mapper {
    public static byte[] toBytes(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static String fromBytes(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Input byte array cannot be null");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}