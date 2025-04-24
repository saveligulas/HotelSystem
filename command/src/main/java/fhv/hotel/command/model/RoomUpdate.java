package fhv.hotel.command.model;

public record RoomUpdate(
    String roomName,
    String description,
    Double price
) {}
