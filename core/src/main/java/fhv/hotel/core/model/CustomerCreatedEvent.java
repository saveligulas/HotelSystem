package fhv.hotel.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerCreatedEvent(LocalDateTime dateTime,
                                   UUID customerUUID,
                                   Long customerNumber,
                                   String firstName,
                                   String lastName,
                                   LocalDate birthday) implements IEventModel {
    public static Event EVENT = Event.CUSTOMER_CREATED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
