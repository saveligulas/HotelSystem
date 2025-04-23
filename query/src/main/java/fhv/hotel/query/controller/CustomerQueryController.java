package fhv.hotel.query.controller;

import fhv.hotel.query.dto.CustomerResponseDTO;
import fhv.hotel.query.projection.CustomerProjection;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerQueryController {

    @Inject
    CustomerProjection customerProjection;

    @GET
    @Path("/getCustomers")
    public Response getCustomers(@QueryParam("name") String name) {
        if (name == null || name.isEmpty()) {
            List<CustomerResponseDTO> customers = customerProjection.getCustomers();

            return Response.ok(customers).build();
        }

        List<CustomerResponseDTO> customers = customerProjection.getCustomersByName(name);
        return Response.ok(customers).build();
    }
}
