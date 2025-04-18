package fhv.hotel.event.protocol.header;

import fhv.hotel.event.utility.Utf8Mapper;
import io.smallrye.mutiny.tuples.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class EventClassConverter {

    public static Map<Byte, String> parse(byte[] data) {
        if (data == null || data.length % 257 != 0) {
            throw new IllegalArgumentException("Data must be non-null and divisible by 257 bytes");
        }

        Map<Byte, String> result = new HashMap<>();

        for (int i = 0; i < data.length; i += 257) {
            byte identifier = data[i];
            byte[] hashBytes = new byte[256];
            System.arraycopy(data, i + 1, hashBytes, 0, 256);
            String hexHash = toHex(hashBytes);
            result.put(identifier, Utf8Mapper.hexHash);
        }

        return result;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
