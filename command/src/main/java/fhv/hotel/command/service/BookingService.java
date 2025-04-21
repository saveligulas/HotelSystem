package fhv.hotel.command.service;

import fhv.hotel.command.model.BookingCreate;
import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class BookingService {

    @Inject
    IBasicRepository<Booking, UUID> bookingRepository;

    @Inject
    IBasicRepository<Customer, UUID> customerRepository;

    @Inject
    IBasicRepository<Room, Long> roomRepository;

    public void createBooking(BookingCreate bookingCreate) {
        Customer customer = customerRepository.findById(bookingCreate.customerId());
        Room room = roomRepository.findById(bookingCreate.roomNumber());

        Booking booking = new Booking(
            UUID.randomUUID(),
            Booking.ID_GENERATOR.incrementAndGet(),
            false,
            false,
            room,
            customer,
            bookingCreate.startDate(),
            bookingCreate.endDate()
        );

        bookingRepository.save(booking);
        customer.addBooking(booking);
        room.addBooking(booking);

        customerRepository.update(customer);
        roomRepository.update(room);
    }

    public Booking getBooking(UUID id) {
        return bookingRepository.findById(id);
    }

    public void payBooking(UUID id) {
        Booking booking = bookingRepository.findById(id);
        Booking updatedBooking = new Booking(
            booking.uuid(),
            booking.bookingNumber(),
            true,
            booking.cancelled(),
            booking.room(),
            booking.customer(),
            booking.startDate(),
            booking.endDate()
        );
        bookingRepository.update(updatedBooking);
    }

    public void cancelBooking(UUID id) {
        Booking booking = bookingRepository.findById(id);
        Booking updatedBooking = new Booking(
            booking.uuid(),
            booking.bookingNumber(),
            booking.paid(),
            true,
            booking.room(),
            booking.customer(),
            booking.startDate(),
            booking.endDate()
        );
        bookingRepository.update(updatedBooking);
    }
}
