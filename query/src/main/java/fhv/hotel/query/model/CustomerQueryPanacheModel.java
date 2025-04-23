package fhv.hotel.query.model;

import fhv.hotel.query.dto.CustomerResponseDTO;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class CustomerQueryPanacheModel extends PanacheEntityBase {

    @Id
    public UUID customerUUID;
    public LocalDateTime dateTime;
    public Long customerNumber;
    public String firstName;
    public String lastName;
    public LocalDate birthday;

    public CustomerResponseDTO toDTO() {
        return new CustomerResponseDTO(
                this.dateTime,
                this.customerNumber,
                this.firstName,
                this.lastName,
                this.birthday
        );
    }
}
