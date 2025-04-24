package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.BookingCancelledEvent;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
public class BookingCancelledConsumer implements IConsumeEvent<BookingCancelledEvent> {
    
    @Inject
    BookingServicePanache bookingServicePanache;
    
    @Override
    @Transactional
    public void consume(BookingCancelledEvent event) {
        // Find bookings with the given room number
        List<BookingQueryPanacheModel> bookings = BookingQueryPanacheModel.find(
                "roomNumber = ?1", 
                event.roomNumber()
        ).list();
        
        if (bookings.isEmpty()) {
            Log.warn("No bookings found for room number: " + event.roomNumber());
            return;
        }
        
        // Update the cancelled status for each booking
        for (BookingQueryPanacheModel booking : bookings) {
            booking.cancelled = true;
            
            // Use the service to update the booking
            bookingServicePanache.updateBooking(booking);
            
            Log.info("Updated booking with UUID: " + booking.uuid + " as cancelled");
        }
    }

    @Override
    public Class<BookingCancelledEvent> getEventClass() {
        return BookingCancelledEvent.class;
    }
}
