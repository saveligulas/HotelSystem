package fhv.hotel.query.service;

import fhv.hotel.query.model.CustomerQueryPanacheModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

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
}
