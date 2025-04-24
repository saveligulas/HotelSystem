package fhv.hotel.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RoomBookedEvent(LocalDateTime localDateTime,
                              UUID customerUUID,
                              Long bookingNumber,
                              Boolean paid,
                              Boolean cancelled,
                              Long roomNumber,
                              LocalDate startDate,
                              LocalDate endDate) implements IEventModel {
    public static final Event EVENT = Event.ROOM_BOOKED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
