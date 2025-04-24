package fhv.hotel.command;

import fhv.hotel.command.model.BookingCreate;
import fhv.hotel.command.model.domain.Booking;
import fhv.hotel.command.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/booking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    BookingService bookingService;

    @POST
    public UUID createBooking(BookingCreate bookingCreate) {
        return bookingService.createBooking(bookingCreate);
    }

    @GET
    @Path("/{id}")
    public Booking getBooking(@PathParam("id") UUID id) {
        return bookingService.getBooking(id);
    }
    
    @PUT
    @Path("/{id}/pay")
    public void payBooking(@PathParam("id") UUID id) {
        bookingService.payBooking(id);
    }
    
    @PUT
    @Path("/{id}/cancel")
    public void cancelBooking(@PathParam("id") UUID id) {
        bookingService.cancelBooking(id);
    }
}
