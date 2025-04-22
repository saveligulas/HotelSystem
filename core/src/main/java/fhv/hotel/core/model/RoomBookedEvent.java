package fhv.hotel.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomBookedEvent(LocalDateTime localDateTime,
                              UUID eventUUID,
                              Long roomNumber,
                              UUID customerUUID) implements IEventModel {
    public static final Event EVENT = Event.ROOM_BOOKED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
