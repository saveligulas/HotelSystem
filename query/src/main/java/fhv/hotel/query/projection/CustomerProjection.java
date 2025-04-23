package fhv.hotel.query.projection;

import fhv.hotel.query.dto.CustomerResponseDTO;
import fhv.hotel.query.model.CustomerQueryPanacheModel;
import fhv.hotel.query.service.CustomerServicePanache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CustomerProjection {

    @Inject
    CustomerServicePanache customerServicePanache;

    public List<CustomerResponseDTO> getCustomers() {
        return customerServicePanache.getCustomers().stream().map(CustomerQueryPanacheModel::toDTO).toList();
    }

    public List<CustomerResponseDTO> getCustomersByName(String name) {
        return customerServicePanache.getCustomersByName(name).stream().map(CustomerQueryPanacheModel::toDTO).toList();
    }
}
