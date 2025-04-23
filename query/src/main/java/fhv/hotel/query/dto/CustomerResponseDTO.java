package fhv.hotel.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponseDTO(
        LocalDateTime dateTime,
        Long customerNumber,
        String firstName,
        String lastName,
        LocalDate birthday
) {
}
