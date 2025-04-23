package fhv.hotel.event.utility;

public class ByteConverter {

    public static short toShort(byte[] bytes) {
        return toShort(bytes[0], bytes[1]);
    }

    public static short toShort(byte[] bytes, int start, int off) {
        return toShort(bytes[start], bytes[start + off]);
    }

    public static short toShort(byte mostSignificantByte, byte leastSignificantByte) {
        return (short) (((mostSignificantByte & 0xff) << 8) | (leastSignificantByte & 0xff));
    }

    public static int toInt(byte[] bytes) {
        return toInt(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    public static int toInt(byte[] bytes, int start) {
        return toInt(bytes[start], bytes[start + 1], bytes[start + 2], bytes[start + 3]);
    }

    public static int toInt(byte[] bytes, int start, int off) {
        return toInt(bytes[start], bytes[start + off], bytes[start + off * 2], bytes[start + off * 3]);
    }

    public static int toInt(byte b1, byte b2, byte b3, byte b4) {
        return ((b1 & 0xff) << 24) |
                ((b2 & 0xff) << 16) |
                ((b3 & 0xff) << 8)  |
                (b4 & 0xff);
    }

    public static long toLong(byte[] bytes) {
        return toLong(bytes[0], bytes[1], bytes[2], bytes[3],
                bytes[4], bytes[5], bytes[6], bytes[7]);
    }

    public static long toLong(byte[] bytes, int start) {
        return toLong(
                bytes[start], bytes[start + 1], bytes[start + 2], bytes[start + 3],
                bytes[start + 4], bytes[start + 5], bytes[start + 6], bytes[start + 7]
        );
    }

    public static long toLong(byte[] bytes, int start, int off) {
        return toLong(
                bytes[start],
                bytes[start + off],
                bytes[start + off * 2],
                bytes[start + off * 3],
                bytes[start + off * 4],
                bytes[start + off * 5],
                bytes[start + off * 6],
                bytes[start + off * 7]
        );
    }

    public static long toLong(byte b1, byte b2, byte b3, byte b4,
                              byte b5, byte b6, byte b7, byte b8) {
        return ((long)(b1 & 0xff) << 56) |
                ((long)(b2 & 0xff) << 48) |
                ((long)(b3 & 0xff) << 40) |
                ((long)(b4 & 0xff) << 32) |
                ((long)(b5 & 0xff) << 24) |
                ((long)(b6 & 0xff) << 16) |
                ((long)(b7 & 0xff) << 8)  |
                ((long)(b8 & 0xff));
    }

    public static byte[] fromShort(int unsignedShort) {
        if (unsignedShort >>> 16 != 0) {
            throw new IllegalArgumentException("value out of range for 16‑bit unsigned: " + unsignedShort);
        }
        return new byte[] {
                (byte) (unsignedShort >>> 8),
                (byte)  unsignedShort
        };
    }

    public static void fromShort(int unsignedShort, byte[] target, int start, int off) {
        byte[] tmp = fromShort(unsignedShort);
        target[start]       = tmp[0];
        target[start + off] = tmp[1];
    }

    public static byte[] fromInt(long unsignedInt) {
        if (unsignedInt >>> 32 != 0) {                   // range check
            throw new IllegalArgumentException("value out of range for 32‑bit unsigned: " + unsignedInt);
        }
        return new byte[] {
                (byte) (unsignedInt >>> 24),
                (byte) (unsignedInt >>> 16),
                (byte) (unsignedInt >>> 8),
                (byte)  unsignedInt
        };
    }

    public static void fromInt(long unsignedInt, byte[] target, int start, int off) {
        byte[] tmp = fromInt(unsignedInt);
        for (int i = 0; i < 4; i++) {
            target[start + i * off] = tmp[i];
        }
    }

    public static byte[] fromLong(long unsignedLong) {
        return new byte[] {
                (byte) (unsignedLong >>> 56),
                (byte) (unsignedLong >>> 48),
                (byte) (unsignedLong >>> 40),
                (byte) (unsignedLong >>> 32),
                (byte) (unsignedLong >>> 24),
                (byte) (unsignedLong >>> 16),
                (byte) (unsignedLong >>> 8),
                (byte)  unsignedLong
        };
    }

    public static void fromLong(long unsignedLong, byte[] target, int start, int off) {
        byte[] tmp = fromLong(unsignedLong);
        for (int i = 0; i < 8; i++) {
            target[start + i * off] = tmp[i];
        }
    }
}
