package fhv.hotel.query.projection;

import fhv.hotel.query.dto.RoomResponseDTO;
import fhv.hotel.query.model.RoomQueryPanacheModel;
import fhv.hotel.query.service.RoomServicePanache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class RoomProjection {

    @Inject
    RoomServicePanache roomServicePanache;

    public List<RoomResponseDTO> getRooms() {
        return roomServicePanache.getRooms()
                .stream()
                .map(RoomQueryPanacheModel::toDTO)
                .toList();
    }

    public List<RoomResponseDTO> getFreeRooms() {
        return roomServicePanache.getFreeRooms()
                .stream()
                .map(RoomQueryPanacheModel::toDTO)
                .toList();
    }
}
