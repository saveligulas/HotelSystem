package fhv.hotel.query.controller;

import fhv.hotel.query.dto.RoomResponseDTO;
import fhv.hotel.query.projection.RoomProjection;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomQueryController {

    @Inject
    RoomProjection roomProjection;

    @GET
    @Path("/getFreeRooms")
    public Response getFreeRooms() { 
        List<RoomResponseDTO> rooms = roomProjection.getFreeRooms();
        return Response.ok(rooms).build();
    }

    @GET
    @Path("/getRooms")
    public Response getRooms() {
        List<RoomResponseDTO> rooms = roomProjection.getRooms();
        return Response.ok(rooms).build();
    }
}
