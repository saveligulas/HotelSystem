package fhv.hotel.query.model;

import fhv.hotel.query.dto.RoomResponseDTO;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RoomQueryPanacheModel extends PanacheEntityBase {

    @Id
    public Long roomNumber;
    public String roomName;
    public String description;
    public Double price;

    public RoomQueryPanacheModel() {
    }

    public RoomQueryPanacheModel(Long roomNumber, String roomName, String description, Double price) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.description = description;
        this.price = price;
    }

    public RoomResponseDTO toDTO() {
        return new RoomResponseDTO(
                this.roomNumber,
                this.roomName,
                this.description,
                this.price
        );
    }
}
