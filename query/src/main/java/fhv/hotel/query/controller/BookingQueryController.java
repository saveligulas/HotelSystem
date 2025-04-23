package fhv.hotel.query.controller;

import fhv.hotel.query.dto.BookingResponseDTO;
import fhv.hotel.query.projection.BookingProjection;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingQueryController {

    @Inject
    BookingProjection bookingProjection;

    @GET
    @Path("/getBookings")
    public Response getBookings(
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {

        LocalDate from = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate to = endDate != null ? LocalDate.parse(endDate) : null;

        List<BookingResponseDTO> bookings = bookingProjection.getBookings(from, to);
        return Response.ok(bookings).build();
    }
}
