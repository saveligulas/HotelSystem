package fhv.hotel.command.repo;

import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class InMemoryBookingRepository implements IBasicRepository<Booking, UUID> {
    private final Map<UUID, Booking> bookingStore = new HashMap<>();

    @Inject
    Provider<InMemoryCustomerRepository> customerRepositoryProvider;

    @Inject
    Provider<InMemoryRoomRepository> roomRepositoryProvider;

    @Override
    public Booking findById(UUID uuid) {
        Booking booking = bookingStore.get(uuid);
        if (booking == null) {
            throw new EntityNotFoundException();
        }
        customerRepositoryProvider.get().retrieveCustomerFromBooking(booking);
        roomRepositoryProvider.get().retrieveRoomFromBooking(booking);
        return booking;
    }

    @Override
    public void save(Booking booking) {
        if (bookingStore.get(booking.uuid()) != null) {
            throw new IllegalArgumentException("booking already exists");
        }

        Booking bookingShallow = new Booking(
                booking.uuid(),
                booking.bookingNumber(),
                booking.paid(),
                booking.cancelled(),
                booking.room().buildShallowModel(booking.room().roomNumber()),
                booking.customer().buildShallowModel(booking.customer().uuid()),
                booking.startDate(),
                booking.endDate()
        );

        bookingStore.put(booking.uuid(), bookingShallow);
    }

    @Override
    public void update(Booking booking) {
        Booking oldBooking = bookingStore.remove(booking.uuid());
        if (oldBooking != null) {
            this.save(booking);
        } else {
            throw new IllegalArgumentException("Cannot update Booking that doesn't exist");
        }
    }

    void retrieveBookingsFromCustomer(Customer customer) {
        List<Booking> bookings = customer.bookings().stream().map(shallowBooking -> {
            Booking booking = bookingStore.get(shallowBooking.uuid());
            booking.setCustomer(customer);
            return booking;
        }).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        customer.setBookings(bookings);
    }

    void retrieveBookingsFromRoom(Room room) {
        List<Booking> bookings = room.bookings().stream().map(shallowBooking -> {
            Booking booking = bookingStore.get(shallowBooking.uuid());
            booking.setRoom(room);
            return booking;
        }).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        room.setBookings(bookings);
    }
}
