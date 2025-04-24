package fhv.hotel.command.model;

import java.time.LocalDate;

public record CustomerUpdate(
    String firstName,
    String lastName,
    LocalDate birthday
) {}