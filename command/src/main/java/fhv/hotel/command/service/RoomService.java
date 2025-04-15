package fhv.hotel.command.service;

import fhv.hotel.command.model.RoomCreate;
import fhv.hotel.command.model.RoomUpdate;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;

@ApplicationScoped
public class RoomService {

    @Inject
    IBasicRepository<Room, Long> roomRepository;

    public void createRoom(RoomCreate roomCreate) {
        Room room = new Room(
            roomCreate.roomNumber(),
            roomCreate.roomName(),
            roomCreate.description(),
            new ArrayList<>()
        );
        roomRepository.save(room);
    }

    public Room getRoom(Long roomNumber) {
        return roomRepository.findById(roomNumber);
    }
    
    public void updateRoom(Long roomNumber, RoomUpdate roomUpdate) {
        Room existingRoom = roomRepository.findById(roomNumber);
        Room updatedRoom = new Room(
            roomNumber,
            roomUpdate.roomName(),
            roomUpdate.description(),
            existingRoom.bookings()
        );
        roomRepository.update(updatedRoom);
    }
}