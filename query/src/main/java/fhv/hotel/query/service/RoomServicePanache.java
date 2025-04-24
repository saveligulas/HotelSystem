package fhv.hotel.query.service;

import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.model.RoomQueryPanacheModel;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationScoped
public class RoomServicePanache {

    public List<RoomQueryPanacheModel> getRooms() {
        return RoomQueryPanacheModel.listAll();
    }

    public List<RoomQueryPanacheModel> getFreeRooms() {
        List<RoomQueryPanacheModel> allRooms = getRooms();

        List<Long> bookedRoomNumbers = BookingQueryPanacheModel.list("cancelled = false")
                .stream()
                .map(booking -> ((BookingQueryPanacheModel) booking).roomNumber)
                .toList();
        
        // returning all rooms that have no bookings for now
        return allRooms.stream()
                .filter(room -> !bookedRoomNumbers.contains(room.roomNumber))
                .toList();
    }

    public Optional<RoomQueryPanacheModel> getRoomByNumber(Long roomNumber) {
        if (roomNumber == null) {
            throw new IllegalArgumentException("Room number cannot be null.");
        }
        return RoomQueryPanacheModel.findByIdOptional(roomNumber);
    }

    @Transactional
    public void createRoom(RoomQueryPanacheModel model) {
        model.persist();
    }

    @Transactional
    public void updateRoom(RoomQueryPanacheModel updatedRoom) {
        RoomQueryPanacheModel currentRoom = getRoomByNumber(updatedRoom.roomNumber)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));

        currentRoom.roomName = updatedRoom.roomName;
        currentRoom.description = updatedRoom.description;
        currentRoom.price = updatedRoom.price;

        Panache.getEntityManager().flush();
    }
}
