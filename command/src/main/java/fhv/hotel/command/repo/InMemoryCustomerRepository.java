package fhv.hotel.command.repo;

import fhv.hotel.command.model.Customer;
import fhv.hotel.core.repo.IBasicRepository;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class InMemoryCustomerRepository implements IBasicRepository<Customer, UUID> {
    @Override
    public Customer findById(UUID uuid) {
        return null;
    }

    @Override
    public void save(Customer customer) {

    }

    @Override
    public void update(Customer customer) {

    }
}
