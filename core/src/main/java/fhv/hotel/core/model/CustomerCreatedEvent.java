package fhv.hotel.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerCreatedEvent(LocalDateTime dateTime,
                                   UUID customerUUID,
                                   Long customerNumber,
                                   String firstName,
                                   String lastName,
                                   LocalDate birthday,
                                   String address) implements IEventModel {
    public static final Event EVENT = Event.CUSTOMER_CREATED;

    @Override
    public Event getEvent() {
        return EVENT;
    }
}
