package fhv.hotel.command.model;

import java.time.LocalDate;
import java.util.UUID;

public record BookingCreate(
    UUID customerId,
    Long roomNumber,
    LocalDate startDate,
    LocalDate endDate
) {}
