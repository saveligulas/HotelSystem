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

    public BookingQueryPanacheModel() {

    }

    public BookingQueryPanacheModel(UUID uuid, Long bookingNumber, Boolean paid, Boolean cancelled, Long roomNumber, UUID customerUUID, LocalDate startDate, LocalDate endDate) {
        this.uuid = uuid;
        this.bookingNumber = bookingNumber;
        this.paid = paid;
        this.cancelled = cancelled;
        this.roomNumber = roomNumber;
        this.customerUUID = customerUUID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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
