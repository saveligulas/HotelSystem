package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.CustomerUpdatedEvent;
import fhv.hotel.query.model.CustomerQueryPanacheModel;
import fhv.hotel.query.service.CustomerServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class CustomerUpdatedConsumer implements IConsumeEvent<CustomerUpdatedEvent> {
    @Inject
    CustomerServicePanache customerServicePanache;

    @Override
    public void consume(CustomerUpdatedEvent event) {
        CustomerQueryPanacheModel customerModel = mapEventToModel(event);
        customerServicePanache.updateCustomer(customerModel);
        Log.info("Updated customer with UUID: " + customerModel.customerUUID);
    }

    private CustomerQueryPanacheModel mapEventToModel(CustomerUpdatedEvent event) {
        CustomerQueryPanacheModel model = new CustomerQueryPanacheModel(
            event.customerUUID(),
            event.dateTime(),
            event.customerNumber(),
            event.firstName(),
            event.lastName(),
            event.birthday()
        );
        
        return model;
    }

    @Override
    public Class<CustomerUpdatedEvent> getEventClass() {
        return CustomerUpdatedEvent.class;
    }
}
