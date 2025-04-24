package fhv.hotel.query.dto;

import java.time.LocalDate;

public record BookingResponseDTO(
        Long bookingNumber,
        Boolean paid,
        Boolean cancelled,
        Long roomNumber,
        LocalDate startDate,
        LocalDate endDate,
        String paymentOption
) {}
