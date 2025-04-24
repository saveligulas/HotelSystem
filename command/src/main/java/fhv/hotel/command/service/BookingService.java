package fhv.hotel.command.service;

import fhv.hotel.command.model.BookingCreate;
import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.BookingCancelledEvent;
import fhv.hotel.core.model.BookingPaidEvent;
import fhv.hotel.core.model.RoomBookedEvent;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class BookingService {

    @Inject
    IBasicRepository<Booking, UUID> bookingRepository;

    @Inject
    IBasicRepository<Customer, UUID> customerRepository;

    @Inject
    IBasicRepository<Room, Long> roomRepository;
    
    @Inject
    IPublishEvent eventPublisher;

    public UUID createBooking(BookingCreate bookingCreate) {
        Customer customer = customerRepository.findById(bookingCreate.customerId());
        Room room = roomRepository.findById(bookingCreate.roomNumber());

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking(
            bookingId,
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
        
        eventPublisher.publish(new RoomBookedEvent(
            LocalDateTime.now(),
            booking.uuid(), // Include booking UUID
            customer.uuid(),
            booking.bookingNumber(),
            booking.paid(),
            booking.cancelled(),
            room.roomNumber(),
            booking.startDate(),
            booking.endDate()
        ));
        
        return bookingId;
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
        
        eventPublisher.publish(new BookingPaidEvent(
            LocalDateTime.now(),
            booking.uuid(),
            booking.room().roomNumber()
        ));
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
        
        eventPublisher.publish(new BookingCancelledEvent(
            LocalDateTime.now(),
            booking.uuid(), // Using the actual booking UUID 
            booking.room().roomNumber()
        ));
    }
}
