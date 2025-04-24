package fhv.hotel.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCancelledEvent(LocalDateTime dateTime,
                                    UUID bookingUUID,
                                    Long roomNumber) implements IEventModel {
    public static final Event EVENT = Event.BOOKING_CANCELLED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}