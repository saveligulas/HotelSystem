package fhv.hotel.command.model;

public record RoomCreate(
    Long roomNumber,
    String roomName,
    String description
) {}
