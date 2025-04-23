package fhv.hotel.query.projection;

import fhv.hotel.query.dto.BookingResponseDTO;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class BookingProjection {

    @Inject
    BookingServicePanache bookingServicePanache;

    public List<BookingResponseDTO> getBookings(LocalDate from, LocalDate to) {
        return bookingServicePanache.getBookings(from, to)
                .stream()
                .map(BookingQueryPanacheModel::toDTO)
                .toList();
    }
}
