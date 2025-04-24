package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.query.model.CustomerQueryPanacheModel;
import fhv.hotel.query.service.CustomerServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CustomerCreatedConsumer implements IConsumeEvent<CustomerCreatedEvent> {
    @Inject
    CustomerServicePanache customerServicePanache;

    @Override
    public void consume(CustomerCreatedEvent event) {
        CustomerQueryPanacheModel customerModel = mapEventToModel(event);
        customerServicePanache.createCustomer(customerModel);
        Log.info("Created customer with UUID: " + customerModel.customerUUID);
    }

    private CustomerQueryPanacheModel mapEventToModel(CustomerCreatedEvent event) {
        return new CustomerQueryPanacheModel(
            event.customerUUID(),
            event.dateTime(),
            event.customerNumber(),
            event.firstName(),
            event.lastName(),
            event.birthday()
        );
    }

    @Override
    public Class<CustomerCreatedEvent> getEventClass() {
        return CustomerCreatedEvent.class;
    }
}
