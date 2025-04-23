package fhv.hotel.temporary;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import io.quarkus.logging.Log;
import jakarta.inject.Singleton;

@Singleton
public class TestConsumer implements IConsumeEvent<CustomerCreatedEvent> {
    @Override
    public void consume(CustomerCreatedEvent event) {
        Log.info("Consumer received CustomerCreatedEvent: " + event);
    }

    @Override
    public Class<CustomerCreatedEvent> getEventClass() {
        return CustomerCreatedEvent.class;
    }
}
