package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.RoomBookedEvent;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RoomBookedEventConsumer implements IConsumeEvent<RoomBookedEvent> {
    @Inject
    BookingServicePanache bookingServicePanache;

    @Override
    public void consume(RoomBookedEvent event) {
        BookingQueryPanacheModel bookingModel = mapEventToModel(event);
        bookingServicePanache.createBooking(bookingModel);
        Log.info("Created booking with UUID: " + bookingModel.uuid + " for room: " + bookingModel.roomNumber);
    }
    
    private BookingQueryPanacheModel mapEventToModel(RoomBookedEvent event) {
        return new BookingQueryPanacheModel(
            event.bookingUUID(), // Use the booking UUID from the event
            event.bookingNumber(),
            event.paid(),
            event.cancelled(),
            event.roomNumber(),
            event.customerUUID(),
            event.startDate(),
            event.endDate(),
            event.paymentOption()
        );
    }

    @Override
    public Class<RoomBookedEvent> getEventClass() {
        return RoomBookedEvent.class;
    }
}
