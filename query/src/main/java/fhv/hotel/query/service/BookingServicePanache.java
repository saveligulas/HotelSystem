package fhv.hotel.query.service;

import fhv.hotel.query.model.BookingQueryPanacheModel;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class BookingServicePanache {

    public List<BookingQueryPanacheModel> getBookings(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return BookingQueryPanacheModel.listAll();
        }

        return BookingQueryPanacheModel.find(
                "fromDate <= ?1 and toDate >= ?2",
                to, from
        ).list();
    }

    public Optional<BookingQueryPanacheModel> getBookingByUUID(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }
        return BookingQueryPanacheModel.findByIdOptional(uuid);
    }

    @Transactional
    public void createBooking(BookingQueryPanacheModel model) {
        model.persist();
    }

    @Transactional
    public void updateBooking(BookingQueryPanacheModel updatedBooking) {
        BookingQueryPanacheModel currentBooking = getBookingByUUID(updatedBooking.uuid)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        currentBooking.bookingNumber = updatedBooking.bookingNumber;
        currentBooking.paid = updatedBooking.paid;
        currentBooking.cancelled = updatedBooking.cancelled;
        currentBooking.roomNumber = updatedBooking.roomNumber;
        currentBooking.customerUUID = updatedBooking.customerUUID;
        currentBooking.startDate = updatedBooking.startDate;
        currentBooking.endDate = updatedBooking.endDate;

        Panache.getEntityManager().flush();
    }
}
