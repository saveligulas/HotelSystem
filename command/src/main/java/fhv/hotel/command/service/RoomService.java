package fhv.hotel.command.service;

import fhv.hotel.command.model.RoomCreate;
import fhv.hotel.command.model.RoomUpdate;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.RoomCreatedEvent;
import fhv.hotel.core.model.RoomUpdatedEvent;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ApplicationScoped
public class RoomService {

    @Inject
    IBasicRepository<Room, Long> roomRepository;
    
    @Inject
    IPublishEvent eventPublisher;

    public Long createRoom(RoomCreate roomCreate) {
        Long roomNumber = roomCreate.roomNumber();
        Room room = new Room(
            roomNumber,
            roomCreate.roomName(),
            roomCreate.description(),
            roomCreate.price(),
            new ArrayList<>()
        );
        roomRepository.save(room);
        
        eventPublisher.publish(new RoomCreatedEvent(
            LocalDateTime.now(),
            room.roomNumber(),
            room.roomName(),
            room.description(),
            room.price()
        ));
        
        return roomNumber;
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
            roomUpdate.price(),
            existingRoom.bookings()
        );
        roomRepository.update(updatedRoom);
        
        eventPublisher.publish(new RoomUpdatedEvent(
            LocalDateTime.now(),
            updatedRoom.roomNumber(),
            updatedRoom.roomName(),
            updatedRoom.description(),
            updatedRoom.price()
        ));
    }
}