package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.BookingPaidEvent;

public class BookingPaidConsumer implements IConsumeEvent<BookingPaidEvent> {
    @Override
    public void consume(BookingPaidEvent event) {

    }

    @Override
    public Class<BookingPaidEvent> getEventClass() {
        return null;
    }
}
