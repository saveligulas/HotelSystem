package fhv.hotel.query.service;

import fhv.hotel.query.model.CustomerQueryPanacheModel;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerServicePanache {

    public List<CustomerQueryPanacheModel> getCustomers() {
        return CustomerQueryPanacheModel.listAll();
    }

    public List<CustomerQueryPanacheModel> getCustomersByName(String name) {
        if (name == null || name.isEmpty()) {
            return CustomerQueryPanacheModel.listAll();
        }

        return CustomerQueryPanacheModel.find(
                "lower(firstName) like ?1 or lower(lastName) like ?1",
                "%" + name.toLowerCase() + "%"
        ).list();
    }

    public Optional<CustomerQueryPanacheModel> getCustomerByUUID(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        Optional<CustomerQueryPanacheModel> customer = CustomerQueryPanacheModel.findByIdOptional(uuid);

        if (customer.isEmpty()) {
            throw new NoSuchElementException("Customer with UUID " + uuid + " not found.");
        }

        return customer;
    }

    @Transactional
    public void createCustomer(CustomerQueryPanacheModel customer) {
        customer.persist();
    }

    @Transactional
    public void updateCustomer(CustomerQueryPanacheModel updatedCustomer) {
        CustomerQueryPanacheModel currentCustomer = getCustomerByUUID(updatedCustomer.customerUUID).orElseThrow();

        currentCustomer.dateTime = updatedCustomer.dateTime;
        currentCustomer.customerNumber = updatedCustomer.customerNumber;
        currentCustomer.firstName = updatedCustomer.firstName;
        currentCustomer.lastName = updatedCustomer.lastName;
        currentCustomer.birthday = updatedCustomer.birthday;

        Panache.getEntityManager().flush();
    }
}
