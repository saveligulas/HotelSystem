package fhv.hotel.command;

import fhv.hotel.command.model.Customer;
import fhv.hotel.command.model.CustomerCreate;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/customer")
public class CustomerResource {

    @Inject
    IBasicRepository<Customer, UUID> customerRepository;

    @Inject
    IPublishEvent<CustomerCreatedEvent> customerCreatedPublisher;

    @POST
    @Consumes("application/json")
    public void createCustomer(CustomerCreate customerCreate) {
        Customer customer = new Customer(UUID.randomUUID(), Customer.ID_GENERATOR.incrementAndGet(), customerCreate.firstName(), customerCreate.lastName(), customerCreate.birthday());
        customerRepository.save(customer);
        customerCreatedPublisher.publish(new CustomerCreatedEvent(LocalDateTime.now(), customer.uuid(), customer.customerNumber(), customer.firstName(), customer.lastName(), customer.birthday()));
    }
}
