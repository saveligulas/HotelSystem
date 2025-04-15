package fhv.hotel.command.model;

import java.time.LocalDate;

public record CustomerCreate(
    String firstName,
    String lastName,
    LocalDate birthday
) {}