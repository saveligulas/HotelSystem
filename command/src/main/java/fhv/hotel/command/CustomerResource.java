package fhv.hotel.command;

import fhv.hotel.command.model.CustomerCreate;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    public void createCustomer(CustomerCreate customerCreate) {
        customerService.createCustomer(customerCreate);
    }

    @GET
    public Customer getCustomer(@QueryParam("id") UUID id) {
        return customerService.getCustomer(id);
    }
}