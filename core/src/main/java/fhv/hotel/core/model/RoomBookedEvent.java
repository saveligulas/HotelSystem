package fhv.hotel.core.model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record RoomBookedEvent(LocalDateTime localDateTime,
                              UUID eventUUID,
                              UUID roomUUID,
                              UUID customerUUID) implements IEventModel {
    public static Event EVENT = Event.ROOM_BOOKED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
