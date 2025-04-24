package fhv.hotel.command.controller;

import fhv.hotel.command.model.RoomCreate;
import fhv.hotel.command.model.RoomUpdate;
import fhv.hotel.command.model.domain.Room;
import fhv.hotel.command.service.RoomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/room")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @Inject
    RoomService roomService;

    @POST
    public Long createRoom(RoomCreate roomCreate) {
        return roomService.createRoom(roomCreate);
    }

    @GET
    @Path("/{roomNumber}")
    @Operation(summary = "Only for testing purposes")
    public Room getRoom(@PathParam("roomNumber") Long roomNumber) {
        return roomService.getRoom(roomNumber);
    }
    
    @PUT
    @Path("/{roomNumber}")
    public void updateRoom(@PathParam("roomNumber") Long roomNumber, RoomUpdate roomUpdate) {
        roomService.updateRoom(roomNumber, roomUpdate);
    }
}
