package fhv.hotel.query.event;

import fhv.hotel.core.event.IConsumeEvent;
import fhv.hotel.core.model.RoomUpdatedEvent;
import fhv.hotel.query.model.RoomQueryPanacheModel;
import fhv.hotel.query.service.RoomServicePanache;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RoomUpdatedConsumer implements IConsumeEvent<RoomUpdatedEvent> {
    @Inject
    RoomServicePanache roomServicePanache;

    @Override
    public void consume(RoomUpdatedEvent event) {
        RoomQueryPanacheModel roomModel = mapEventToModel(event);
        roomServicePanache.updateRoom(roomModel);
        Log.info("Updated room with number: " + roomModel.roomNumber);
    }

    private RoomQueryPanacheModel mapEventToModel(RoomUpdatedEvent event) {
        return new RoomQueryPanacheModel(
            event.roomNumber(),
            event.roomName(),
            event.description(),
            event.price()
        );
    }

    @Override
    public Class<RoomUpdatedEvent> getEventClass() {
        return RoomUpdatedEvent.class;
    }
}
