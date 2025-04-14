package fhv.hotel.command.model;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public record Customer(UUID uuid, Long customerNumber, String firstName, String lastName, LocalDate birthday) {
    public static AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());
}
