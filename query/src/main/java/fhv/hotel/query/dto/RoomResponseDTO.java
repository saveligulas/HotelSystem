package fhv.hotel.query.dto;

public record RoomResponseDTO(
        Long roomNumber,
        String roomName,
        String description,
        Double price
) {}
