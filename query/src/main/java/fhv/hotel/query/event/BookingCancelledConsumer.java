package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.BookingCancelledEvent;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class BookingCancelledConsumer implements IConsumeEvent<BookingCancelledEvent> {
    
    @Inject
    BookingServicePanache bookingServicePanache;
    
    @Override
    @Transactional
    public void consume(BookingCancelledEvent event) {
        Optional<BookingQueryPanacheModel> bookingOptional = bookingServicePanache.getBookingByUUID(event.bookingUUID());
        
        if (bookingOptional.isEmpty()) {
            Log.warn("No booking found with UUID: " + event.bookingUUID());
            return;
        }

        BookingQueryPanacheModel booking = bookingOptional.get();
        booking.cancelled = true;

        bookingServicePanache.updateBooking(booking);
        
        Log.info("Updated booking with UUID: " + booking.uuid + " as cancelled");
    }

    @Override
    public Class<BookingCancelledEvent> getEventClass() {
        return BookingCancelledEvent.class;
    }
}
