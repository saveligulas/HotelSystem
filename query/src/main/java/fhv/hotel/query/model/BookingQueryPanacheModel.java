package fhv.hotel.query.model;

import fhv.hotel.query.dto.BookingResponseDTO;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class BookingQueryPanacheModel extends PanacheEntityBase {

    @Id
    public UUID uuid;
    public Long bookingNumber;
    public Boolean paid;
    public Boolean cancelled;
    public Long roomNumber;
    public UUID customerUUID;
    public LocalDate startDate;
    public LocalDate endDate;

    public BookingResponseDTO toDTO() {
        return new BookingResponseDTO(
                this.bookingNumber,
                this.paid,
                this.cancelled,
                this.roomNumber,
                this.startDate,
                this.endDate
        );
    }
}
