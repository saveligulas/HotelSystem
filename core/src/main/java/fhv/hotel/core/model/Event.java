package fhv.hotel.core.model;

public enum Event {
    ROOM_BOOKED,
    BOOKING_CANCELLED,
    CUSTOMER_CREATED,
    CUSTOMER_UPDATED,
    BOOKING_PAID,
    ROOM_CREATED,
    ROOM_UPDATED;

    public byte getOrdinalByte() {
        return (byte) this.ordinal();
    }
}
