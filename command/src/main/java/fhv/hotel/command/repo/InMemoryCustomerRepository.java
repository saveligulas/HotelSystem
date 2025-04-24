package fhv.hotel.command.repo;

import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class InMemoryCustomerRepository implements IBasicRepository<Customer, UUID> {
    private final Map<UUID, Customer> customerStore = new HashMap<>();

    @Inject
    Provider<InMemoryBookingRepository> inMemoryBookingRepositoryProvider;

    @Override
    public Customer findById(UUID uuid) {
        Customer customer = customerStore.get(uuid);
        if (customer == null) {
            throw new EntityNotFoundException();
        }
        inMemoryBookingRepositoryProvider.get().retrieveBookingsFromCustomer(customer);
        return customer;
    }

    @Override
    public void save(Customer customer) {
        if (customerStore.get(customer.uuid()) != null) {
            throw new IllegalArgumentException("Customer already exists");
        }

        Customer customerShallow = new Customer(
                customer.uuid(),
                customer.customerNumber(),
                customer.firstName(),
                customer.lastName(),
                customer.birthday(),
                customer.address(),
                customer.bookings() == null ? new ArrayList<>() : 
                    customer.bookings().stream()
                        .filter(b -> b != null)
                        .map(b -> b.buildShallowModel(b.uuid()))
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        customerStore.put(customer.uuid(), customerShallow);
    }

    @Override
    public void update(Customer customer) {
        Customer oldCustomer = customerStore.remove(customer.uuid());
        if (oldCustomer != null) {
            this.save(customer);
        } else {
            throw new IllegalArgumentException("Cannot update Customer that doesn't exist");
        }
    }

    public void retrieveCustomerFromBooking(Booking booking) {
        if (booking == null || booking.customer() == null) {
            return;
        }
        
        UUID customerId = booking.customer().getID();
        if (customerId != null) {
            Customer customer = customerStore.get(customerId);
            if (customer != null) {
                booking.setCustomer(customer);
            }
        }
    }
}