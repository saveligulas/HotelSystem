package fhv.hotel.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPaidEvent(LocalDateTime localDateTime,
                               UUID bookingUUID,
                               Long roomNumber,
                               String paymentOption) implements IEventModel {
    public static final Event EVENT = Event.BOOKING_PAID;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
