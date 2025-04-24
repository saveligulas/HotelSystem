package fhv.hotel.core.model;

import java.time.LocalDateTime;

public record RoomUpdatedEvent(LocalDateTime dateTime,
                               Long roomNumber,
                               String roomName,
                               String description,
                               Double price) implements IEventModel {
    public static final Event EVENT = Event.ROOM_UPDATED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
