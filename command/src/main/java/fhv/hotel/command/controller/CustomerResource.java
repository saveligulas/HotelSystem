package fhv.hotel.command.controller;

import fhv.hotel.command.model.CustomerCreate;
import fhv.hotel.command.model.CustomerUpdate;
import fhv.hotel.command.model.domain.Customer;
import fhv.hotel.command.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.UUID;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    public UUID createCustomer(CustomerCreate customerCreate) {
        return customerService.createCustomer(customerCreate);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Only for testing purposes")
    public Customer getCustomer(@PathParam("id") UUID id) {
        return customerService.getCustomer(id);
    }
    
    @PUT
    @Path("/{id}")
    public void updateCustomer(@PathParam("id") UUID id, CustomerUpdate customerUpdate) {
        customerService.updateCustomer(id, customerUpdate);
    }
}