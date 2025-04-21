package fhv.hotel.event.utility;

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
            result.put(identifier, HexConverter.toHex(hashBytes));
        }

        return result;
    }
}
