package fhv.hotel.temporary;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import jakarta.inject.Singleton;

@Singleton
public class TestConsumer implements IConsumeEvent<CustomerCreatedEvent> {
    @Override
    public void consume(CustomerCreatedEvent event) {
        System.out.println("Customer Created Event" + event);
    }

    @Override
    public Class<CustomerCreatedEvent> getEventClass() {
        return CustomerCreatedEvent.class;
    }
}
