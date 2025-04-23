package fhv.hotel.query.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerQueryController {

    @GET
    @Path("/getCustomers/{name}")
    public Response getCustomers(@QueryParam("name") String name) {
        if (name == null || name.isEmpty()) {
            List<CustomerResponseDTO> customers = customerProjection.getAllCustomers();

            return Response.ok(customers).build();
        }

        List<CustomerResponseDTO> customers = customerProjection.getCustomerByName(name);
        return Response.ok(customers).build();
    }
}
