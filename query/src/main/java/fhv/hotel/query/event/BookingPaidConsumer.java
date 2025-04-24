package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.BookingPaidEvent;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Singleton
public class BookingPaidConsumer implements IConsumeEvent<BookingPaidEvent> {
    
    @Inject
    BookingServicePanache bookingServicePanache;
    
    @Override
    @Transactional
    public void consume(BookingPaidEvent event) {
        Optional<BookingQueryPanacheModel> bookingOptional = bookingServicePanache.getBookingByUUID(event.bookingUUID());
        
        if (bookingOptional.isEmpty()) {
            Log.warn("No booking found with UUID: " + event.bookingUUID());
            return;
        }

        BookingQueryPanacheModel booking = bookingOptional.get();
        booking.paid = true;
        // Set the payment option from the event
        booking.paymentOption = event.paymentOption();

        bookingServicePanache.updateBooking(booking);
        
        Log.info("Updated booking with UUID: " + booking.uuid + " as paid");
    }

    @Override
    public Class<BookingPaidEvent> getEventClass() {
        return BookingPaidEvent.class;
    }
}
