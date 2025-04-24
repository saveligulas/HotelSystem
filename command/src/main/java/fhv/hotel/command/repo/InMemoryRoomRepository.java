package fhv.hotel.command.repo;

import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class InMemoryRoomRepository implements IBasicRepository<Room, Long> {
    private final Map<Long, Room> roomStore = new HashMap<>();

    @Inject
    Provider<InMemoryBookingRepository> bookingRepositoryProvider;

    @Inject
    Provider<InMemoryCustomerRepository> customerRepositoryProvider;

    @Override
    public Room findById(Long roomNumber) {
        Room room = roomStore.get(roomNumber);
        if (room == null) {
            throw new EntityNotFoundException();
        }
        bookingRepositoryProvider.get().retrieveBookingsFromRoom(room);
        return room;
    }

    @Override
    public void save(Room room) {
        if (roomStore.get(room.roomNumber()) != null) {
            throw new IllegalArgumentException("Room already exists");
        }

        Room roomShallow = new Room(
                room.roomNumber(),
                room.roomName(),
                room.description(),
                room.price(),
                room.bookings() == null ? new ArrayList<>() : 
                    room.bookings().stream()
                        .filter(b -> b != null)
                        .map(b -> b.buildShallowModel(b.uuid()))
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        roomStore.put(room.roomNumber(), roomShallow);
    }

    @Override
    public void update(Room room) {
        Room oldRoom = roomStore.remove(room.roomNumber());
        if (oldRoom != null) {
            this.save(room);
        } else {
            throw new IllegalArgumentException("Cannot update Room that doesn't exist");
        }
    }

    public void retrieveRoomFromBooking(Booking booking) {
        if (booking == null || booking.room() == null) {
            return;
        }
        
        Long roomNumber = booking.room().roomNumber();
        if (roomNumber != null) {
            Room room = roomStore.get(roomNumber);
            if (room != null) {
                booking.setRoom(room);
            }
        }
    }
}