package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.query.service.CustomerServicePanache;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CustomerCreatedConsumer implements IConsumeEvent<CustomerCreatedEvent> {
    @Inject
    CustomerServicePanache customerServicePanache;

    @Override
    public void consume(CustomerCreatedEvent event) {
        customerServicePanache.createCustomer(event);
    }

    @Override
    public Class<CustomerCreatedEvent> getEventClass() {
        return CustomerCreatedEvent.class;
    }
}
