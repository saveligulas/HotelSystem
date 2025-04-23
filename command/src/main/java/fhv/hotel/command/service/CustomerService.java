package fhv.hotel.command.service;

import fhv.hotel.command.model.CustomerCreate;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@ApplicationScoped
public class CustomerService {

    @Inject
    IBasicRepository<Customer, UUID> customerRepository;

    @Inject
    IPublishEvent eventPublisher;

    public void createCustomer(CustomerCreate customerCreate) {
        Customer customer = new Customer(
            UUID.randomUUID(), 
            Customer.ID_GENERATOR.incrementAndGet(), 
            customerCreate.firstName(), 
            customerCreate.lastName(), 
            customerCreate.birthday(), 
            new ArrayList<>()
        );
        customerRepository.save(customer);
        eventPublisher.publish(new CustomerCreatedEvent(
            LocalDateTime.now(), 
            customer.uuid(), 
            customer.customerNumber(), 
            customer.firstName(), 
            customer.lastName(), 
            customer.birthday()
        ));
    }

    public Customer getCustomer(UUID id) {
        return customerRepository.findById(id);
    }
}