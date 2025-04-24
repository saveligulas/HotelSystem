package fhv.hotel.command.service;

import fhv.hotel.command.model.CustomerCreate;
import fhv.hotel.command.model.CustomerUpdate;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.core.event.IPublishEvent;
import fhv.hotel.core.model.CustomerCreatedEvent;
import fhv.hotel.core.model.CustomerUpdatedEvent;
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

    public UUID createCustomer(CustomerCreate customerCreate) {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
            customerId, 
            Customer.ID_GENERATOR.incrementAndGet(), 
            customerCreate.firstName(), 
            customerCreate.lastName(), 
            customerCreate.birthday(), 
            customerCreate.address(),
            new ArrayList<>()
        );

        customerRepository.save(customer);

        eventPublisher.publish(new CustomerCreatedEvent(
            LocalDateTime.now(), 
            customer.uuid(), 
            customer.customerNumber(), 
            customer.firstName(), 
            customer.lastName(), 
            customer.birthday(),
            customer.address()
        ));
        
        return customerId;
    }

    public Customer getCustomer(UUID id) {
        return customerRepository.findById(id);
    }
    
    public void updateCustomer(UUID id, CustomerUpdate customerUpdate) {
        Customer existingCustomer = customerRepository.findById(id);
        Customer updatedCustomer = new Customer(
            existingCustomer.uuid(),
            existingCustomer.customerNumber(),
            customerUpdate.firstName(),
            customerUpdate.lastName(),
            customerUpdate.birthday(),
            customerUpdate.address(),
            existingCustomer.bookings()
        );
        customerRepository.update(updatedCustomer);
        
        eventPublisher.publish(new CustomerUpdatedEvent(
            LocalDateTime.now(),
            updatedCustomer.uuid(),
            updatedCustomer.customerNumber(),
            updatedCustomer.firstName(),
            updatedCustomer.lastName(),
            updatedCustomer.birthday(),
            updatedCustomer.address()
        ));
    }
}