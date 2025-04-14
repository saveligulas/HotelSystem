package fhv.hotel.command.event;

import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerCreatedPublisher implements IPublishEvent<CustomerCreatedEvent> {


    @Override
    public void publish(CustomerCreatedEvent event) {
        Log.info("Customer Created Event: " + event);
    }
}
