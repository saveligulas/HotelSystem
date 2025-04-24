package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.RoomBookedEvent;
import fhv.hotel.query.model.BookingQueryPanacheModel;
import fhv.hotel.query.service.BookingServicePanache;
import jakarta.inject.Inject;

public class RoomBookedEventConsumer implements IConsumeEvent<RoomBookedEvent> {
    @Inject
    BookingServicePanache bookingServicePanache;


    @Override
    public void consume(RoomBookedEvent event) {
        bookingServicePanache.createBooking(event);
    }

    @Override
    public Class<RoomBookedEvent> getEventClass() {
        return RoomBookedEvent.class;
    }
}
